package item

import (
	"../config"
	"../constant"
	"log"
	"../util"
	"gopkg.in/mgo.v2/bson"
	"../mongo"
	"time"
)

type Message struct {
	Id bson.ObjectId `bson:"_id,omitempty" json:"id"`
	Body string
	Read int8
	CreateDate time.Time
}

type Item struct {
	Id bson.ObjectId `bson:"_id,omitempty" json:"id"`
	Title string
	Description string
	ItemType int
	RegisterDate int64
	Date string
	CategoryId string
	CategoryTitle string
	CityId string
	ImageExt string
	CityTitle string
	ProvinceId string
	ProvinceTitle string
	Mobile string
	Latitude float64
	Longitude float64
	Address string
	Approved string
	ApprovedTime int64
	Email string
	TelegramId string
	OwnerId string
	Messages []Message
}


func   Items(title string, categoryId string, provinceId string,
	cityId string, itemType string, approved string, ownerId string) []Item {
	var items []Item
	log.Printf("Connecting to  %s  database \n",config.Config.MongoDatabaseName )
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection)
	q := bson.M{}

	if title != ""{
		q["title"] =  title
	}
	if categoryId !="" {
		q["categoryid"] = categoryId
	}
	if provinceId != ""{
		q["provinceid"] = provinceId
	}
	if cityId != "" {
		q["cityid"] = cityId
	}
	if itemType != "" {
		q["itemtype"] = itemType
	}
	if approved != ""{
		q["approved"] = approved
	}
	if ownerId !=  ""{
		q["ownerid"] =  ownerId
	}
	log.Printf("List items criteria: %v",q)
	err := collection.Find(q).Sort("-registerdate").All(&items)
	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}

func NewItem(id string, title string, category string, categoryTitle string, description string, date string,
	itemType int, imageExt string, cityId string, cityTitle string, provinceId string, provinceTitle string,
	mobile string, latitude float64, longitude float64, address string, email string, telegramId string,
	ownerId string) (string,error) {
	newItem  :=  Item{
		Title:title,
		CategoryId:category,
		CategoryTitle:categoryTitle,
		Description:description,
		Date:date,
		ItemType:itemType,
		RegisterDate: time.Now().UnixNano(),
		ImageExt: imageExt,
		CityId: cityId,
		CityTitle : cityTitle,
		ProvinceId : provinceId,
		ProvinceTitle : provinceTitle,
		Mobile:mobile,
		Latitude:latitude,
		Longitude:longitude,
		Address:address,
		Approved:"false",
		Email : email,
		TelegramId : telegramId,
		OwnerId: ownerId,
	}
	log.Printf("Item Id: ",id)
	log.Printf("New Item Values: %v",newItem)
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection)
	itemId := bson.NewObjectId()
	if id != ""{
		itemId = bson.ObjectIdHex(id)
	}
	_,err:=collection.UpsertId(itemId,newItem)
	if(err != nil){
		log.Printf("error on inserting new item : %s",err)
		return "",err
	}
	log.Printf("New item inserted successfully, ID:%s  Values:%v", itemId,newItem)
	return itemId.Hex(),nil
}

func ItemById(id string) *Item {
	log.Printf("Finding Item by Id %s",id)
	var foundedItem Item
	q := bson.M{}
	q["_id"] = bson.ObjectIdHex(id)

	mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection).FindId(bson.ObjectIdHex(id)).One(&foundedItem)
	log.Printf("Founded Item: %v",foundedItem)
	return &foundedItem
}

func approveItem(id string) {
	// Update
	q := bson.M{}
	q["_id"] = bson.ObjectIdHex(id)
	change := bson.M{"$set": bson.M{"approved": "true", "approvedtime": util.GetCurrentMilis() }}
	err := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection).Update(q, change)
	if err != nil {
		log.Printf("Error in approving  item %d  error: %v \n",err)
	}else {
		log.Printf("Item %d successfully approved",id)
	}
}

func addMessageToItem(id string, body string) error {
	newId:= bson.NewObjectId()
	message  :=  Message{
		Id:newId,
		Body:body,
		CreateDate:time.Now(),
	}
	q:= bson.M{}
	q["_id"] = bson.ObjectIdHex(id)
	change  := bson.M{"$push": bson.M{"messages": message}}
	err := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(constant.ItemCollection).Update(q, change)
	if err != nil {
		log.Printf("Error in adding new message error: %v \n",err)
		return err
	}else {
		log.Printf("Message added to item ",id)
	}
	return nil
}
