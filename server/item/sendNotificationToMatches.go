package item

import (
	"github.com/labstack/gommon/log"
	"gopkg.in/mgo.v2/bson"
	"../util"
	"../mongo"
	"../constant"
	"../config"
	"time"
)

func sendNotificationToMatches(id string) {
	log.Printf("Start searching  for match items for %s",id)
	items  := getMatchedItems(id)
	log.Printf("Matched Items: \n %v",items)
}

func getMatchedItems(id string) ([]Item){
	var items []Item
	registeredItem := ItemById(id)
	q:= bson.M{}

	if registeredItem.ItemType == "1"{
		q["itemtype"] = "2"
	} else {
		q["itemtype"] = "1"
	}
	q["categoryid"] = registeredItem.CategoryId
	q["approved"] = "true"
	q["registerdate"] = bson.M{ "$gte" : time.Now().AddDate(0,-1,0).UnixNano() }
	minLat, minLng, maxLat, maxLng  :=  util.LatLongInDistance(registeredItem.Latitude, registeredItem.Longitude,1)
	q["latitude"] = bson.M{"$gte" : minLat, "$lte" : maxLat }
	q["longitude"] = bson.M{"$gte" : minLng, "$lte" : maxLng }
	log.Printf("Query for finding match with item %s : Query : %v",id,q)
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection)
	err := collection.Find(q).Sort("-registerdate").All(&items)
	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}



