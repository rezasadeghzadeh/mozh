package mongo

import (
	"gopkg.in/mgo.v2"
	"../config"
)

var MongoSession *mgo.Session

func Setup(){
	session, err := mgo.Dial(config.Config.MongoServerIP)
	if err != nil {
		panic(err)
	}
	session.SetMode(mgo.Monotonic, true)
	MongoSession =  session
}

