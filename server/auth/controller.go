package auth

import (
	"github.com/kataras/iris"
	"github.com/labstack/gommon/log"
	"github.com/dgrijalva/jwt-go"
	"time"
	"../constant"
	"gopkg.in/mgo.v2/bson"
)
type RequestResponse struct {
	Status int
	Message string
	Token string
}

func RegisterAuthRoutes()  {
	authUser()
	newUser()
}

func authUser() {
	iris.Get("/auth/validateUserPass", func(ctx *iris.Context) {
		response  :=  RequestResponse{}
		user:= parseUser(ctx)
		firebaseToke  := ctx.URLParam("FirebaseToken")
		log.Printf("Start authentication user  %v",user)
		userRecord := userByUsernameAndPass(user.Username, user.Password)
		log.Printf("result of  founded users: %v",userRecord)
		if userRecord == nil {
			response.Status = 0
			response.Message = "invalid user/pass"
			ctx.JSON(iris.StatusOK,response)
			return
		}
		//update firebase Token
		updateFirebaseToken(userRecord.Id,firebaseToke)
		token, err :=   generateToken(userRecord)
		if err != nil{
			ctx.JSON(iris.StatusInternalServerError,"error in generating token")
			log.Printf("Eror in generating token %v",err)
			return
		}
		response.Token=token
		response.Status = 1
		response.Message = "success"
		ctx.JSON(iris.StatusOK,response)
	})
}

func generateToken(user *User) (string,error){
	token := jwt.New(jwt.SigningMethodHS256)
	claims := make(jwt.MapClaims)
	log.Printf("Generating token for user ",user)
	claims["userid"] = user.Id
	claims["exp"] = time.Now().Add(time.Hour * time.Duration(constant.ExpireLongTime)).Unix()
	claims["iat"] = time.Now().Unix()
	token.Claims = claims
	tokenString, err := token.SignedString(constant.JWTSecretKey)
	if err != nil{
		return "",err
	}
	log.Printf("User  %s authenticated successfully ",user.Username)
	return tokenString, nil
}

func newUser() {
	iris.Get("/auth/newUser", func(ctx *iris.Context) {
		log.Printf("Start registering new user")
		res := RequestResponse{Status:0}
		email  := ctx.URLParam("Email")
		if email == ""{
			log.Printf("Invalid Email")
			res.Message = "Invalid Email"
			ctx.JSON(iris.StatusOK,res)
			return
		}

		//already  exists
		userRecord := userByUsername(email)
		log.Printf("result of  founded users: %v",userRecord)
		if userRecord != nil {
			res.Status = 2
			res.Message = "email already exists"
			ctx.JSON(iris.StatusOK,res)
			return
		}

		pass := ctx.URLParam("Password")
		firebaseToke  := ctx.URLParam("FirebaseToken")
		user:= User{
			Username:email,
			Password:[]byte(pass),
			Firebasetoken: firebaseToke,
		}
		id,err := upsertUser(&user)
		if err != nil{
			log.Printf("Error in registering/updating  user, %v",err)
			res.Message = "Error in registering new user"
			ctx.JSON(iris.StatusOK,res)
			return
		}
		user.Id  = bson.ObjectIdHex(id)
		token, err :=   generateToken(&user)
		if err != nil{
			ctx.JSON(iris.StatusInternalServerError,"error in generating token")
			log.Printf("Eror in generating token %v",err)
			return
		}
		res.Token=token
		res.Status = 1
		res.Message = "success"
		ctx.JSON(iris.StatusOK,res)

	})
}

func parseUser(ctx  *iris.Context) *User {
	user := User{
		Username: ctx.PostValue("Email"),
		Password: []byte(ctx.PostValue("Password")),
	}
	return &user
}

func GetCurrentUserId(ctx *iris.Context) string {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Recovered : %s", r)
		}
	}()
	token :=JwtMiddleware.Get(ctx)
	log.Printf("Token: " , token )
	claims := token.Claims.(jwt.MapClaims)
	userId := claims["userid"].(string)
	log.Printf("Current User Id: " + userId )
	return userId
}

