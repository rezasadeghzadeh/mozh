package item

import (
	"github.com/labstack/gommon/log"
	"gopkg.in/mgo.v2/bson"
	"time"
)

func sendNotificationToMatches(id string) {
	log.Printf("Start searching  for match items for %s",id)
	registeredItem := ItemById(id)
	q:= bson.M{}

	if registeredItem.ItemType == 1{
		q["type"] = 2
	} else {
		q["type"] = 1
	}
	q["categoryid"] = registeredItem.CategoryId
	q["approved"] = "true"
	q["registerdate"] = bson.M{ "$gte" : time.Now().AddDate(0,-1,0).UnixNano() }

}


