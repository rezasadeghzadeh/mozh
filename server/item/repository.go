package item

import (
	"../config"
	"../R"
	"log"
	"../util"
	"gopkg.in/mgo.v2/bson"
	"../mongo"
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
	Latitude string
	Longitude string
	Address string
	Approved string
	ApprovedTime int64
	Email string
	TelegramId string
}


func   Items(title string, categoryId string, provinceId string,
	cityId string, itemType string, approved string) []Item {
	var items []Item
	log.Printf("Connecting to  %s  database \n",config.Config.MongoDatabaseName )
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection)
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
	log.Printf("List items criteria: %v",q)
	err := collection.Find(q).Sort("-registerdate").All(&items)
	if(err != nil){
		log.Printf(err.Error())
	}
	return items
}

func NewItem(title string, category string, categoryTitle string, description string, date string,
	itemType string, imageExt string, cityId string, cityTitle string, provinceId string, provinceTitle string,
	mobile string, latitude string, longitude string, address string, email string, telegramId string ) (string,error) {
	newItem  :=  Item{
		Title:title,
		CategoryId:category,
		CategoryTitle:categoryTitle,
		Description:description,
		Date:date,
		ItemType:itemType,
		RegisterDate: util.GetCurrentMilis(),
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
	}
	log.Printf("New Item Values: %v",newItem)
	collection  := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection)
	newId  := bson.NewObjectId()
	_,err:=collection.UpsertId(newId,newItem)
	if(err != nil){
		log.Printf("error on inserting new item : %s",err)
		return "",err
	}
	log.Printf("New item inserted successfully, ID:%s  Values:%v",newId,newItem)
	return newId.Hex(),nil
}

func ItemById(id string) *Item {
	log.Printf("Finding Item by Id %s",id)
	var foundedItem Item
	q := bson.M{}
	q["_id"] = bson.ObjectIdHex(id)

	mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection).FindId(bson.ObjectIdHex(id)).One(&foundedItem)
	log.Printf("Founded Item: %v",foundedItem)
	return &foundedItem
}

func approveItem(id string) {
	// Update
	q := bson.M{}
	q["_id"] = bson.ObjectIdHex(id)
	change := bson.M{"$set": bson.M{"approved": "true", "approvedtime": util.GetCurrentMilis() }}
	err := mongo.MongoSession.DB(config.Config.MongoDatabaseName).C(R.ItemCollection).Update(q, change)
	if err != nil {
		log.Printf("Error in approving  item %d  error: %v \n",err)
	}else {
		log.Printf("Item %d successfully approved",id)
	}
}

