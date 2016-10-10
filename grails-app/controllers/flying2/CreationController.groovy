package flying2

import img.ImageOperation
import org.springframework.beans.factory.annotation.Value
import upLoad.ImgUploader

class CreationController {
    @Value('${filePath.tempImgPath}')
    String tempImgPath
    @Value('${filePath.showImgPath}')
    String showImgPath

    def creationService

    def createProduct() {
        params.type!="photo"&&params.type!="video"?params.type="photo":""
        String user = session.user.username
        String tempFolderName = System.currentTimeMillis()
        String folder = user+"_"+params.type+"_"+tempFolderName
        String newTempFolder = showImgPath+File.separator+folder
        new File(newTempFolder).mkdir()
        [folder: folder]
//        String tempFile = "o_1463409038736.JPG"
//        println tempFile.substring(2, tempFile.length())
//        println "tempImgPath is:"+tempImgPath
    }

    def uploadImg() {
        String info
        String oImg = ImgUploader.upLoadImg(showImgPath+File.separator+params.folder, request)
        String upLoadedImg = showImgPath+File.separator+params.folder+File.separator+oImg
        int oImgWidth = ImageOperation.getImgSize(upLoadedImg).width
//        中等图片
//        int mediumMaxWidth = 1200
        int mediumMaxWidth = 800
        String noDecorationImgName = oImg.substring(2, oImg.length())
        if (oImgWidth > mediumMaxWidth) {
            String mediumImg = "medium_"+noDecorationImgName
            info = noDecorationImgName
            ImageOperation.cutImage(mediumMaxWidth, upLoadedImg, showImgPath+File.separator+params.folder+File.separator+mediumImg)
//            大图片
//            int largeMaxWidth = 1920
            int largeMaxWidth = 1000
            if (oImgWidth > largeMaxWidth) {
                String largeImg = "large_"+noDecorationImgName
                ImageOperation.cutImage(largeMaxWidth, upLoadedImg, showImgPath+File.separator+params.folder+File.separator+largeImg)
            }
        }else{
            info = "sizeNotReached"
        }
        render info
    }

    def cropCoverImg() {
        String coverImg = "c_"+params.imgName
        String newImgLocation = showImgPath+File.separator+params.folder+File.separator+coverImg
        String imgLocation = showImgPath+File.separator+params.folder+File.separator+"medium_"+params.imgName
//        String newWholeImgLocation = showImgPath+"\\"+newImgLocation
        int oImgWidth = ImageOperation.getImgSize(imgLocation).width
        def scaling = oImgWidth/params.iWidth.toDouble()
        ImageOperation.cropImage(
                imgLocation,
                newImgLocation,
                400,
                (params.x1.toDouble()*scaling).toInteger(),
                (params.y1.toDouble()*scaling).toInteger(),
                (params.x2.toDouble()*scaling).toInteger(),
                (params.y2.toDouble()*scaling).toInteger()
        )
        render coverImg
    }
//    def upLoadBeautyImg() {
//        String fName = params.fName
//        String imgPath = showImgPath+File.separator+fName
//        def uploadedImgName = ImgUploader.upLoadImg(imgPath, request)
//        String prefix = "s_"
//        def uploadedImgLocation = imgPath+File.separator+uploadedImgName
//        int oImgWidth = ImageOperation.getImgWidth(uploadedImgLocation)
//        int maxWidth = 810
//        def showImgName = prefix+uploadedImgName
//        def finalImgLocation = imgPath+File.separator+showImgName
//        if (oImgWidth > maxWidth) {
//            ImageOperation.cutImage(maxWidth, uploadedImgLocation, finalImgLocation)
//            render fName+File.separator+showImgName
//        }else{
//            render fName+File.separator+uploadedImgName
//        }
//    }

    def doCreate() {
        def tip = [:]
        try {
            String type = params.type
//            new Video(params).save()
            def imgPath = [showImgPath: showImgPath, showImgPath: showImgPath]
            def userId = session.user.id
            if (type == "photo") {
                creationService.createPhoto(params, userId, imgPath)
            }else if (type == "video") {
                creationService.createVideo(params, userId, imgPath)
            }
            tip.status = "success"
            tip.content = "成功啦！"
        } catch (Exception e) {
            e.printStackTrace()
            tip.status = "failed"
            tip.content = "保存失败！"
        }
        render(view: "/creation/statusTips", model: [tip: tip])
    }
}
