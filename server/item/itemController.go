package item

import (
	"log"
	"github.com/kataras/iris"
	"io"
	"os"
	"bufio"
	"path/filepath"
	"../config"
	"image/jpeg"
	"github.com/nfnt/resize"
	"../auth"
)

func NewItemHandler()  {

	iris.Post("/item/add" ,auth.JwtMiddleware.Serve, func(ctx  *iris.Context) {
		log.Printf("%v",ctx.Request.Header)
		log.Println("Start serving /items/new request")
		id := ctx.PostValue("Id")
		title:= ctx.PostValue("Title")
		category := ctx.PostValue("Category")
		categoryTitle := ctx.PostValue("CategoryTitle")
		description :=ctx.PostValue("Description")
		date := ctx.PostValue("Date")
		itemType := ctx.PostValue("ItemType")
		cityId := ctx.PostValue("CityId")
		cityTitle := ctx.PostValue("CityTitle")
		provinceId := ctx.PostValue("ProvinceId")
		provinceTitle := ctx.PostValue("ProvinceTitle")
		mobile := ctx.PostValue("Mobile")
		latitude := ctx.PostValue("Latitude")
		longitude := ctx.PostValue("Longitude")
		address := ctx.PostValue("Address")
		email := ctx.PostValue("Email")
		telegramId := ctx.PostValue("TelegramId");
		ownerId := auth.GetCurrentUserId(ctx)
		imageHeader,errUpload := ctx.FormFile("ImageFile")
		imageExt := ""
		if errUpload == nil {
			if imageHeader.Filename != "" {
				imageExt = filepath.Ext(imageHeader.Filename)
			}
		}else {
			log.Printf("Error on uploaded file Error: %s",errUpload)
		}
		id,err := NewItem(id,title, category, categoryTitle, description, date, itemType, imageExt,
		cityId, cityTitle, provinceId, provinceTitle, mobile, latitude, longitude, address, email, telegramId, ownerId)
		if err != nil {
			log.Printf("Error on inserting new Item: %s",err)
			return ;
		}
		if err != nil{
			log.Printf("%s",err)
			return
		}

		if errUpload == nil{
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
		}

		//send  notification for item registrars that are  looking up
		sendNotificationToMatches(id)
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

func ListItemHandler()  {
	iris.Get("/item/list",func(ctx *iris.Context)	{
		log.Println("Start serving /items request")
		title:= ctx.URLParam("Title")
		category := ctx.URLParam("Category")
		itemType := ctx.URLParam("ItemType")
		cityId := ctx.URLParam("CityId")
		provinceId := ctx.URLParam("ProvinceId")
		approved := ctx.URLParam("Approved")
		ownerId := ctx.URLParam("OwnerId")
		log.Printf("Category: %s",category)
		items := Items(title,category,provinceId,cityId,itemType,approved, ownerId)
		ctx.Response.Header.Add("Access-Control-Allow-Origin","*")
		ctx.JSON(iris.StatusOK,	items)
	})
}

func DetailItemHandler()  {
	iris.Get("/item/detail",func(ctx *iris.Context)	{
		log.Println("Start serving /item/detail request")
		id:= ctx.URLParam("Id")
		item := ItemById(id)
		ctx.JSON(iris.StatusOK,	item)
	})
}

func ApproveItemHandler(){
	iris.Get("/item/approve",func(ctx  *iris.Context){
		id:=  ctx.URLParam("Id")
		log.Printf("Approving item Id\t %s",id)
		if id  == ""{
			log.Printf("Approving item Id is null. skip")
			return
		}
		approveItem(id)
		ctx.Response.Header.Add("Access-Control-Allow-Origin","*")
		ctx.JSON(iris.StatusOK,	"{status:1}")
	})
}

func MyItemsHandler()  {
	iris.Get("/item/my", auth.JwtMiddleware.Serve, func(ctx *iris.Context) {
		currentUserId  := auth.GetCurrentUserId(ctx)
		if currentUserId != ""{
			items := Items("","","","","","", currentUserId)
			ctx.Response.Header.Add("Access-Control-Allow-Origin","*")
			ctx.JSON(iris.StatusOK,	items)
		}
	})
}

func AddMessageToItemHandler()  {
	iris.Get("/item/message/add", func(ctx *iris.Context) {
		res := auth.RequestResponse{}
		id :=  ctx.URLParam("Id")
		if id == ""{
			return
		}
		body  := ctx.URLParam("Body")
		err:=addMessageToItem(id,body)
		if err != nil{
			log.Printf("Error in adding  message to item, Error: %#v",err)
			res.Status = 0
		}else {
			res.Status = 1
		}
		ctx.JSON(iris.StatusOK,res)
	})

}