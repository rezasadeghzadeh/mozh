package category

import (
	"log"
	"../config"
	"../R"
	"gopkg.in/mgo.v2/bson"
	"../mongo"
)

type Category struct {
	Id    bson.ObjectId `bson:"_id"`
	Title string
}

func  Categories() []Category {
	var categories []Category
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(R.CategoryCollection)
	err := collection.Find(nil).All(&categories)
	if(err != nil){
		log.Printf(err.Error())
	}
	return categories
}
