package item

import (
	"gopkg.in/mgo.v2"
	"../config"
	"../R"
	"log"
	"time"
	"gopkg.in/mgo.v2/bson"
)

type Item struct {
	Id bson.ObjectId `bson:"_id,omitempty" json:"id"`
	Title string
	Description string
	ItemType string
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
}

type mongoSession struct {
	Session *mgo.Session
}

func   Items(mongoSession *mgo.Session, title string, categoryId string, provinceId string, cityId string, itemType string) []Item {
	var items []Item
	log.Printf("Connecting to  %s  database \n",config.Config.MongoDatabaseName )
	collection  := mongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection)
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
	log.Printf("List items criteria: %v",q)
	err := collection.Find(q).Sort("-registerdate").All(&items)
	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}

func NewItem(mongoSession *mgo.Session, title string, category string, categoryTitle string, description string, date string,
	itemType string, imageExt string, cityId string, cityTitle string, provinceId string, provinceTitle string,
	mobile string ) (string,error) {
	newItem  :=  Item{
		Title:title,
		CategoryId:category,
		CategoryTitle:categoryTitle,
		Description:description,
		Date:date,
		ItemType:itemType,
		RegisterDate:time.Now().UnixNano() / (int64(time.Millisecond) / int64(time.Nanosecond) ),
		ImageExt: imageExt,
		CityId: cityId,
		CityTitle : cityTitle,
		ProvinceId : provinceId,
		ProvinceTitle : provinceTitle,
		Mobile:mobile,
	}
	log.Printf("New Item Values: %v",newItem)
	collection  := mongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection)
	newId  := bson.NewObjectId()
	_,err:=collection.UpsertId(newId,newItem)
	if(err != nil){
		log.Printf("error on inserting new item : %s",err)
		return "",err
	}
	log.Printf("New item inserted successfully, ID:%s  Values:%v",newId,newItem)
	return newId.Hex(),nil
}

func ItemById(session *mgo.Session, id string) *Item {
	log.Printf("Finding Item by Id %s",id)
	var foundedItem Item
	q := bson.M{}
	q["_id"] = bson.ObjectIdHex(id)

	session.DB(config.Config.MongoDatabaseName).C(R.ItemCollection).FindId(bson.ObjectIdHex(id)).One(&foundedItem)
	log.Printf("Founded Item: %v",foundedItem)
	return &foundedItem
}