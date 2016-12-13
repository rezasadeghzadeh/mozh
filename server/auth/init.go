package auth

import (
	jwtmiddleware "github.com/iris-contrib/middleware/jwt"
	"github.com/dgrijalva/jwt-go"
	"../constant"
)


var JwtMiddleware *jwtmiddleware.Middleware
func init() {
	JwtMiddleware = jwtmiddleware.New(jwtmiddleware.Config{
		ValidationKeyGetter: func(token *jwt.Token) (interface{}, error) {
			return []byte(constant.JWTSecretKey), nil
		},
		Debug:true,
		SigningMethod: jwt.SigningMethodHS256,
	})
}
