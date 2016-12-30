package item

import (
	"github.com/labstack/gommon/log"
	"gopkg.in/mgo.v2/bson"
	"../mongo"
	"../constant"
	"../config"
	"time"
	"github.com/kataras/iris"
	"../util"
)

func sendNotificationToMatches(id string) {
	log.Printf("Start searching  for match items for %s",id)
	items  := getMatchedItems(id)
	log.Printf("Matched Items: %d",len(items))
	log.Printf("Matched Items: \n %#+v",items)
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

	latMin, lngMin, latMax, lngMax := util.LatLongInDistance(registeredItem.Latitude, registeredItem.Longitude)
	log.Printf("%10f %10f %10f %10f",latMin, lngMin, latMax, lngMax )
	q["latitude"] = bson.M{"$gte": latMin, "$lte":  latMax}
	q["longitude"] = bson.M{"$gte": lngMin, "$lte":  lngMax}

	//calculate  latitude & longitude
/*	earthRadius := 6371.01
	distance := 1000.0
	itemLocation := geo.FromRadians(registeredItem.Latitude, registeredItem.Longitude)
	boundingCoordinates,err := itemLocation.BoundingCoordinates(distance, earthRadius)
	if err != nil{
		log.Printf("Error in calculating bounding coordinate for latitude & longitude")
	}else {
		meridian180WithinDistance  := boundingCoordinates[0].GetLongitudeInRadians() > boundingCoordinates[1].GetLongitudeInRadians()

		if meridian180WithinDistance {
			q["$and"] = []interface{}{

				bson.M{"$and": []interface{}{
					bson.M{"latitude": bson.M{"$gte":boundingCoordinates[0].GetLatitudeInRadians()}},
					bson.M{"latitude": bson.M{"$lte":boundingCoordinates[1].GetLatitudeInRadians()}},
				}},

				bson.M{"$or": []interface{}{
					bson.M{"longitude": bson.M{"$gte":boundingCoordinates[0].GetLongitudeInRadians()}},
					bson.M{"longitude": bson.M{"$lte":boundingCoordinates[1].GetLongitudeInRadians()}},
				}},
			}
		}else {
			q["$and"] = []interface{}{

				bson.M{"$and": []interface{}{
					bson.M{"latitude": bson.M{"$gte":boundingCoordinates[0].GetLatitudeInRadians()}},
					bson.M{"latitude": bson.M{"$lte":boundingCoordinates[1].GetLatitudeInRadians()}},
				}},

				bson.M{"$and": []interface{}{
					bson.M{"longitude": bson.M{"$gte":boundingCoordinates[0].GetLongitudeInRadians()}},
					bson.M{"longitude": bson.M{"$lte":boundingCoordinates[1].GetLongitudeInRadians()}},
				}},
			}
		}




	}*/


	log.Printf("Query for finding match with item %s : Query : %#+v",id,q)
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection)
	err := collection.Find(q).Sort("-registerdate").All(&items)
	/*for i,_:= range items{
		if ! math.Acos(math.Sin()* math.Sin(items[i].Latitude) + math.Cos(?) * math.Cos(items[i].Latitude) + math.Cos(items[i].Longitude - ?)) <= {
			//remove item from slice
			items = append(items[:i], items[i+1:]...)
		}
	}*/

	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}

func TestSendNotification(){
	iris.Get("/sendNotification", func(ctx *iris.Context) {
		sendNotificationToMatches("5864061cd11fd73a74f3960e")
	})
}

