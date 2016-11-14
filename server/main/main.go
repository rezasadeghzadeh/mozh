package main

import (
	"fmt"
	"log"
	"../item"
	"../category"
	"gopkg.in/mgo.v2"
	"../config"
	"github.com/kataras/iris"
)

func main()  {
	fmt.Println("Start Mozhdegani server")
	mongoSession  := SessionFactory()
	registerRoutes(mongoSession)
	registerStaticRoutes()
	iris.Listen(":7777")
	/*
	if err := http.ListenAndServe(":7777", nil); err != nil {
		log.Fatal("failed to start server", err)
	}
	*/
}
func registerStaticRoutes() {
	//thumbnail url
	thumbnailUrl :=  config.Config.StaticUrl + config.Config.ThumbnailUrl
	iris.StaticServe(config.Config.ItemThumbnailImagesPath, thumbnailUrl)
}

func SessionFactory() *mgo.Session{
	session, err := mgo.Dial(config.Config.MongoServerIP)
	if err != nil {
		panic(err)
	}
	session.SetMode(mgo.Monotonic, true)
	return session
}

func registerRoutes(mongoSession *mgo.Session)  {
	item.ListItemHandler(mongoSession)
	listCategories(mongoSession)
	item.NewItemHandler(mongoSession)
	item.DetailItemHandler(mongoSession)
}

func listCategories(mongoSession *mgo.Session) {
	iris.Get("/category/list",func(ctx *iris.Context)	{
		log.Println("Start serving /categories request")
		categories := category.Categories(mongoSession)
		ctx.JSON(iris.StatusOK,	categories)
	})
}

