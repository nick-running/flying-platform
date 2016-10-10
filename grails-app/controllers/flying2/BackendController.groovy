package flying2

import grails.converters.JSON
import img.ImageOperation
import org.springframework.beans.factory.annotation.Value
import upLoad.ImgUploader

class BackendController {
    @Value('${filePath.homeImgPath}')
    String homeImgPath

    static defaultAction = "allProductions"
    def backendService

    def allProductions() {
        def products = Product.list()
        int photoCount = Product.countByType("photo")
        int videoCount = Product.countByType("video")
        def recommends = Recommend.list()
        [products: products, photoCount: photoCount, videoCount: videoCount, recommends: recommends]
    }

    def delProduct() {
        def m
        try {
            def p = Product.get(params.pid)
            def r = Recommend.findByProduct(p)
            if (r) {
                m = [status: "recommend"]
            }else{
                p.delete()
                m = [status: "ok"]
            }
        } catch (Exception e) {
            m = [status: "failed"]
        }
        render m as JSON
    }

    def addToPreRecommend() {
        def m
        try {
            int status = backendService.addToPreRecommend(params)
            m = [status: "ok", recommendStatus: status]
        } catch (Exception e) {
            m = [status: "failed"]
        }
        render m as JSON
    }

    def changeRecommendStatus() {
        def m
        try {
            int status = 1
            if (params.isRecommend=="true") {
                status = 0
            }
            def recommend = Recommend.get(params.rId)
            String type = recommend.product.type
            def typeRecommend = Recommend.findAll{
                product.type == type&&status == 1
            }
            if (status==0||typeRecommend.size() < 8) {
                if (recommend) {
                    recommend.status = status
                    recommend.save()
                }
                m = [status: "ok", recommendStatus: status]
            }else{
                status = 2
                m = [status: "ok", recommendStatus: status]
            }
        } catch (Exception e) {
            e.printStackTrace()
            m = [status: "failed"]
        }
        render m as JSON
    }
    def delRecommend() {
        def m = [:]
//        try {
//            def r = flying2.Recommend.get(params.rId)
//            if (r) {
//                def rh = new flying2.RecommendHistory(
//                        createDate: r.createDate,
//                        status: r.status,
//                        orderId: r.orderId,
//                        reason: r.reason,
//                        productName: r.product.name
//                )
//                rh.save()
//                if (rh) {
//                    r.delete()
//                    m = [status: "ok"]
//                }
//            }
//        } catch (Exception e) {
//            m = [status: "failed"]
//        }
        render m as JSON
    }


    def homeManage() {
        params.type!="banner"&&params.type!="recommend"?params.type="banner":""
        if (params.type == "recommend") {
            def recommends = Recommend.list()
            int photoCount = recommends.findAll {it.product.type=="photo"}.size()
            int videoCount = recommends.findAll {it.product.type=="video"}.size()
            [recommends: recommends, photoCount: photoCount, videoCount: videoCount]
        }
    }
    def uploadBannerImg() {
        String info
        String oImg = ImgUploader.upLoadImg(homeImgPath+File.separator, request)
        String upLoadedImg = homeImgPath+File.separator+File.separator+oImg
        int oImgWidth = ImageOperation.getImgSize(upLoadedImg).width
//        中等图片
        String noDecorationImgName = oImg.substring(2, oImg.length())
//            大图片
        int largeMaxWidth = 1920
        if (oImgWidth > largeMaxWidth) {
            info = noDecorationImgName
            String largeImg = "homeBanner_"+noDecorationImgName
            ImageOperation.cutImage(largeMaxWidth, upLoadedImg, homeImgPath+File.separator+File.separator+largeImg)
        }else{
            info = "sizeNotReached"
        }
        render info
    }
}
