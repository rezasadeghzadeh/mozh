package category

import (
	"gopkg.in/mgo.v2"
	"log"
	"../config"
	"../R"
	"gopkg.in/mgo.v2/bson"
)

type Category struct {
	Id    bson.ObjectId `bson:"_id"`
	Title string
}

func  Categories(mongoSession *mgo.Session) []Category {
	var categories []Category
	collection  := mongoSession.DB(config.Config.MongoDatabaseName).C(R.CategoryCollection)
	err := collection.Find(nil).All(&categories)
	if(err != nil){
		log.Printf(err.Error())
	}
	return categories
}
