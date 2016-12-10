package auth

import (
	"github.com/kataras/iris"
	"github.com/labstack/gommon/log"
	"github.com/dgrijalva/jwt-go"
	"time"
	"../constant"
)

func RegisterAuthRoutes()  {
	iris.Get("/user/auth", func(ctx *iris.Context) {
		user:= parseUser(ctx)
		log.Printf("Start authentication user  %v",user)
		userRecord := userByUsernameAndPass(user.Username, user.Password)
		if userRecord == nil {
			ctx.JSON(iris.StatusForbidden,"Error in Authentication")
			return
		}
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
		data := map[string]string{
			"token":tokenString,
		}
		ctx.JSON(iris.StatusOK,data)
	})

	iris.Get("user/new", func(ctx  *iris.Context) {
		user:= parseUser(ctx)
		if user.Username == "" ||  len(user.Password) == 0{
			log.Printf("username  or password is empty")
			return
		}

		log.Printf("registering  new user  %v",user)
		id,err := newUser(user)
		if err != nil{
			log.Printf("Error in saving new user")
		}else {
			log.Printf("user registered with new id  %s",id)
		}
	})
	iris.Get("/secure",JwtMiddleware.Serve, func(ctx *iris.Context) {
		GetCurrentUserId(ctx)

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
	token :=JwtMiddleware.Get(ctx)
	claims := token.Claims.(jwt.MapClaims)
	userId := claims["userid"].(string)
	log.Printf("Current User Id: " + userId )
	return userId
}