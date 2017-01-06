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
	"../auth"
	"github.com/NaySoftware/go-fcm"
	"fmt"

)

func sendNotificationToMatches(id string) {
	log.Printf("Start searching  for match items for %s",id)
	registeredItem := ItemById(id)

	items  := getMatchedItems(id)
	log.Printf("Matched Items: %d",len(items))
	log.Printf("Matched Items: \n %#+v",items)

	//send notification by firebase
	var firebaseDeviceTokens []string
	for _,currentIitem := range items{
		itemOwner := auth.UserById(currentIitem.OwnerId)
		if itemOwner != nil{
			firebaseDeviceTokens = append(firebaseDeviceTokens,itemOwner.Firebasetoken)
		}
	}

	if len(firebaseDeviceTokens)  == 0{
		log.Printf("Not found any matched Item to send notification")
		return
	}
	//add item id
	data := map[string]string{
		constant.Id:registeredItem.Id.Hex(),
	}

	c := fcm.NewFcmClient(constant.ServerKey)
	c.NewFcmRegIdsMsg(firebaseDeviceTokens, data)
	var NP fcm.NotificationPayload
	var  part1 string
	if registeredItem.ItemType == constant.FoundedType {
		part1= "پیدا شده "
	}else {
		part1= "گم شده "
	}
	part2 := "جدیدی با ویژگی های آگهی شما در مژده ثبت شد."
	NP.Body=  part1 + part2
	c.SetNotificationPayload(&NP)

	status, err := c.Send()


	if err == nil {
		status.PrintResults()
	} else {
		fmt.Println(err)
	}




}

func getMatchedItems(id string) ([]Item){
	var items []Item
	registeredItem := ItemById(id)
	q:= bson.M{}

	if registeredItem.ItemType == constant.FoundedType{
		q["itemtype"] = constant.LostType
	} else {
		q["itemtype"] = constant.FoundedType
	}
	//TODO compatibvle search  with multiple cateogry
	q["categoryids"] = bson.M{"$in" :registeredItem.CategoryIds}
	q["approved"] = "true"
	q["registerdate"] = bson.M{ "$gte" : time.Now().AddDate(0,-1,0).UnixNano() }

	latMin, lngMin, latMax, lngMax := util.LatLongInDistance(registeredItem.Latitude, registeredItem.Longitude)
	log.Printf("%10f %10f %10f %10f",latMin, lngMin, latMax, lngMax )
	q["latitude"] = bson.M{"$gte": latMin, "$lte":  latMax}
	q["longitude"] = bson.M{"$gte": lngMin, "$lte":  lngMax}

	log.Printf("Query for finding match with item %s : Query : %#+v",id,q)
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection)
	err := collection.Find(q).Sort("-registerdate").All(&items)

	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}

func TestSendNotification(){
	iris.Get("/sendNotification", func(ctx *iris.Context) {
		sendNotificationToMatches("58663bacd11fd75d1592b866")
	})
}

