package item

import (
	"log"
	"gopkg.in/mgo.v2"
	"github.com/kataras/iris"
	"io"
	"os"
	"bufio"
	"path/filepath"
	"../config"
	"image/jpeg"
	"github.com/nfnt/resize"
)

func NewItemHandler(mongoSession *mgo.Session)  {
	iris.Post("/item/add", func(ctx  *iris.Context) {
		log.Println("Start serving /items/new request")

		title:= ctx.PostValue("Title")
		category := ctx.PostValue("Category")
		description :=ctx.PostValue("Description")
		date := ctx.PostValue("Date")
		itemType := ctx.PostValue("ItemType")
		cityId := ctx.PostValue("CityId")
		cityTitle := ctx.PostValue("CityTitle")
		provinceId := ctx.PostValue("ProvinceId")
		provinceTitle := ctx.PostValue("ProvinceTitle")
		imageHeader,err := ctx.FormFile("ImageFile")
		imageExt := ""
		if  imageHeader.Filename != ""{
			imageExt= filepath.Ext(imageHeader.Filename)
		}
		id,err := NewItem(mongoSession,title, category, description, date, itemType, imageExt,
		cityId, cityTitle, provinceId, provinceTitle)
		if err != nil {
			log.Printf("Error on inserting new Item: %s",err)
			return ;
		}
		if err != nil{
			log.Printf("%s",err)
			return
		}
		imageFile,_ := imageHeader.Open()
		imageReader  :=  bufio.NewReader(imageFile)
		newFileName  := id   + imageExt
		image,err :=  os.OpenFile(config.Config.ItemImagesPath + newFileName,os.O_WRONLY| os.O_CREATE,0666)
		if(err != nil){
			log.Printf("%s",err)
			return
		}
		defer image.Close()
		io.Copy(image, imageReader)
		log.Printf("File  %s  moved to target dicrectory",newFileName)
		//create thumbnail
		createThumbnail(newFileName, newFileName)
	})
}

func createThumbnail(newFileName string, newFileThumbnailName string) {
	log.Printf("Createing thumbnail for  %s" + newFileName)
	file,err  :=  os.Open(config.Config.ItemImagesPath + newFileName)
	if err !=  nil{
		log.Fatal(err)
	}
	img,err  := jpeg.Decode(file)
	if err != nil{
		log.Fatal(err)
	}
	file.Close()

	m := resize.Thumbnail(150,150,img,resize.Lanczos3)
	out,err :=  os.Create(config.Config.ItemThumbnailImagesPath + newFileThumbnailName)
	if err != nil{
		log.Fatal(err)
	}
	defer  out.Close()
	jpeg.Encode(out,m,nil)
	log.Printf("Thumbnail for  %s created successfully",newFileName)
}

func ListItemHandler(mongoSession *mgo.Session)  {
	iris.Get("/item/list",func(ctx *iris.Context)	{
		log.Println("Start serving /items request")
		title:= ctx.URLParam("Title")
		category := ctx.URLParam("Category")
		itemType := ctx.URLParam("ItemType")
		cityId := ctx.URLParam("CityId")
		provinceId := ctx.URLParam("ProvinceId")
		log.Printf("Category: %s",category)
		items := Items(mongoSession,title,category,provinceId,cityId,itemType)
		ctx.JSON(iris.StatusOK,	items)
	})
}