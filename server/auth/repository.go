package auth

import (
	"../mongo"
	"../config"
	"../constant"
	"gopkg.in/mgo.v2/bson"
	"github.com/labstack/gommon/log"
	"golang.org/x/crypto/bcrypt"
)

type User struct {
	Id bson.ObjectId `bson:"_id,omitempty" json:"id"`
	Username string
	Password []byte
}

func userByUsernameAndPass(username string , password []byte) (*User) {
	c := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.UserCollection)
	q := bson.M{}
	q["username"] = username
	user := []User{}
	c.Find(q).All(&user)
	if len(user) == 0 || user[0].Id == ""{ //user not found
		return nil
	}
	if bcrypt.CompareHashAndPassword(user[0].Password,password) ==  nil {
		return &user[0]
	}
	return nil
}

func userByUsername(username string) (*User) {
	q:= bson.M{}
	q["username"] = username
	users := []User{}
	c:= mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.UserCollection)
	c.Find(q).All(&users)
	if len(users) > 0 && users[0].Id != ""{
		return &users[0]
	}
	return nil
}

func upsertUser(user  *User) (string,error){
	userByUsername := userByUsername(user.Username)
	userId:= bson.NewObjectId()
	if userByUsername != nil{
		userId = userByUsername.Id
	}

	c := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.UserCollection)
	hashedPassword, err := bcrypt.GenerateFromPassword(user.Password, bcrypt.DefaultCost)
	if err != nil{
		log.Printf("Error on encrypting pass")
	}
	user.Password =  hashedPassword
	_,err = c.UpsertId(userId,user)
	if err != nil{
		log.Printf("Error  on saving new user: %v",err)
		return "",err
	}
	return userId.Hex(),nil
}
