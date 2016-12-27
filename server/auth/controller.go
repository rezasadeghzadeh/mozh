package auth

import (
	"github.com/kataras/iris"
	"github.com/labstack/gommon/log"
	"github.com/dgrijalva/jwt-go"
	"time"
	"../constant"
	"../util"
	"fmt"
	"strconv"
)
type RequestResponse struct {
	Status int
	Message string
	Token string
}

func RegisterAuthRoutes()  {
	authUser()
	sendPassToEmail()
}


func authUser() {
	iris.Get("/auth/genToken", func(ctx *iris.Context) {
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
		token := jwt.New(jwt.SigningMethodHS256)
		claims := make(jwt.MapClaims)
		claims["userid"] = userRecord.Id
		claims["exp"] = time.Now().Add(time.Hour * time.Duration(constant.ExpireLongTime)).Unix()
		claims["iat"] = time.Now().Unix()
		token.Claims = claims
		tokenString, err := token.SignedString(constant.JWTSecretKey)
		if err != nil{
			ctx.JSON(iris.StatusInternalServerError,"error in generating token")
			log.Printf("Eror in generating token %v",err)
			return
		}
		log.Printf("User  %s authenticated successfully ",user.Username)
		response.Token = tokenString
		response.Status = 1
		response.Message = "success"
		ctx.JSON(iris.StatusOK,response)
	})
}

func sendPassToEmail() {
	iris.Get("/auth/sendPassToEmail", func(ctx *iris.Context) {
		res := RequestResponse{Status:0}
		email  := ctx.URLParam("email")
		if email == ""{
			log.Printf("Invalid Email")
			res.Message = "Invalid Email"
			ctx.JSON(iris.StatusOK,res)
			return
		}

		//send pass to email
		pass := util.Random(100000,999999)
		log.Printf("Sending  email to  %s",email)
		err := util.SendEmail(email,"Mozhdeh New Account",fmt.Sprintf("Your Confirm Code: %d",pass))
		if err != nil{
			log.Printf("Error in sending confirm email, %v",err)
			res.Message = "Error in sending confirm email"
			ctx.JSON(iris.StatusOK,res)
			return
		}
		user:= User{
			Username:email,
			Password:[]byte(strconv.Itoa(pass)),
		}
		_,err = upsertUser(&user)
		if err != nil{
			log.Printf("Error in registering/updating  user, %v",err)
			res.Message = "Error in registering new user"
			ctx.JSON(iris.StatusOK,res)
			return
		}

		res.Status = 1
		res.Message = "Email Sent Successfully"
		log.Printf("Email to  %s sent successfully",email)
		ctx.JSON(iris.StatusOK,res)
	})
}

func parseUser(ctx  *iris.Context) *User {
	user := User{
		Username: ctx.PostValue("username"),
		Password: []byte(ctx.PostValue("password")),
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
	claims := token.Claims.(jwt.MapClaims)
	userId := claims["userid"].(string)
	log.Printf("Current User Id: " + userId )
	return userId
}

