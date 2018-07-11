$(function () {
    $list = $('#fileList');
    var flie_count = 0;

    WebUploader.Uploader.register({
            "before-send": "beforeSend"  //每个分片上传前
        },
        {
            //时间点2：如果有分块上传，则每个分块上传之前调用此函数
            beforeSend: function (block) {
                var file = block.file;
                var fileMd5 = file.wholeMd5;
                var deferred = WebUploader.Deferred();
                $.ajax({
                    type: "POST",
                    url: "/blockIsExist",  //ajax验证每一个分片
                    data: {
                        md5value: fileMd5,  //文件唯一标记
                        chunk: block.chunk  //当前分块下标
                    },
                    cache: false,
                    async: false,  // 与js同步
                    timeout: 1000, //todo 超时的话，只能认为该分片未上传过
                    dataType: "json",
                    success: function (response) {
                        console.info("[" + response + "]");
                        if (response == true) {
                            //分块存在，跳过
                            console.info("block exist");
                            deferred.reject();
                        } else {
                            //分块不存在或不完整，重新发送该分块内容
                            console.info("block not exist");
                            deferred.resolve();
                        }
                    },
                    error: function () {
                        deferred.resolve();
                    }
                });

                // this.owner.options.formData.fileMd5 = fileMd5;
                // deferred.resolve();
                return deferred.promise();
            }

        });


    var uploader = WebUploader.create({
        //设置选完文件后是否自动上传
        auto: false,
        //swf文件路径
        swf: 'static/webuploader/Uploader.swf',
        // 文件接收服务端。
        server: 'BigFileUp',
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',
        chunked: true, //开启分块上传
        chunkSize: 10 * 1024 * 1024,
        chunkRetry: 3,//网络问题上传失败后重试次数
        threads: 1, //上传并发数
        //fileNumLimit :1,
        // fileSizeLimit: 3 * 1024 * 1024 * 1024,//最大2GB
        // fileSingleSizeLimit: 3 * 1024 * 1024 * 1024,
        resize: false//不压缩
        //选择文件类型
        //accept: {
        //    title: 'Video',
        //    extensions: 'mp4,avi',
        //    mimeTypes: 'video/*'
        //}
    });


    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        $list.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + '<button type="button" fileId="' + file.id + '" class="btn btn-danger btn-delete"><span class="glyphicon glyphicon-trash"></span></button></h4>' +
            '<p class="state">正在计算文件MD5...请等待计算完毕后再点击上传！</p>' +
            // '<input type="text" id="rename_' + file.id + '" />' +   // 重命名
            '</div>');
        // console.info("id=file_"+flie_count);
        flie_count++;

        //删除要上传的文件
        //每次添加文件都给btn-delete绑定删除方法
        $(".btn-delete").click(function () {
            //console.log($(this).attr("fileId"));//拿到文件id
            // uploader.removeFile(uploader.getFile($(this).attr("fileId"), true));
            uploader.removeFile($(this).attr("fileId"), true);
            $(this).parent().parent().fadeOut();//视觉上消失了
            $(this).parent().parent().remove();//DOM上删除了
        });
        //uploader.options.formData.guid = WebUploader.guid();//每个文件都附带一个guid，以在服务端确定哪些文件块本来是一个
        //console.info("guid= "+WebUploader.guid());

        //md5计算
        uploader.md5File(file, 0, 10 * 1024 * 1024)
            .progress(function (percentage) {
                console.log('Percentage:', percentage);
            })
            // 完成
            .then(function (fileMd5) { // 完成
                var end = +new Date();
                // console.log("before-send-file  preupload: file.size="+file.size+" file.md5="+fileMd5);
                file.wholeMd5 = fileMd5;//获取到了md5
                //uploader.options.formData.md5value = file.wholeMd5;//每个文件都附带一个md5，便于实现秒传

                $('#' + file.id).find('p.state').text('MD5计算完毕，可以点击上传了');
                // console.info("MD5="+fileMd5);
            });

        /** 添加文件后重新显示暂停按钮 */
        $('#StopBtn').fadeIn('slow');
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');
        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }
        $li.find('p.state').text('上传中');
        $percent.css('width', percentage * 100 + '%');
    });

    //发送前填充数据
    uploader.on('uploadBeforeSend', function (block, data) {
        // block为分块数据。

        // file为分块对应的file对象。
        var file = block.file;
        var fileMd5 = file.wholeMd5;
        // 修改data可以控制发送哪些携带数据。

        // console.info("fileName= "+file.name+" fileMd5= "+fileMd5+" fileId= "+file.id+" chunk="+block.chunk);
        console.info("input file= " + flie_count);
        // 将存在file对象中的md5数据携带发送过去。
        data.md5value = fileMd5;//md5
        data.relativePath = '';
        /** 重命名 */
        // data.fileName_ = $("#rename_" + file.id).val();
        // console.log("fileName_: " + data.fileName_);
        // 删除其他数据
        // delete data.key;
        if (block.chunks > 1) { //文件大于chunksize 分片上传
            data.isChunked = true;
            console.info("data.isChunked= " + data.isChunked);

            //##########################断点续传######################
            // var deferred = WebUploader.Deferred();
            // $.ajax({
            //     type:"POST",
            //     url:"/blockIsExist",  //ajax验证每一个分片
            //     data:{
            //         md5value:fileMd5,  //文件唯一标记
            //         chunk:block.chunk  //当前分块下标
            //     },
            //     cache: false,
            //     async: false,  // 与js同步
            //     timeout: 1000, //todo 超时的话，只能认为该分片未上传过
            //     dataType:"json",
            //     success:function(response){
            //         console.info("["+response+"]");
            //         if(response== true){
            //             //分块存在，跳过
            //             console.info("block exist");
            //             deferred.reject();
            //         }else{
            //             //分块不存在或不完整，重新发送该分块内容
            //             console.info("block not exist");
            //             deferred.resolve();
            //         }
            //     }
            // });
            //##########################断点续传######################

        } else {
            data.isChunked = false;
            console.info("data.isChunked=" + data.isChunked);
        }

    });


    uploader.on('uploadSuccess', function (file) {
        $('#' + file.id).find('p.state').text('已上传');
        $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-success");
        $('#' + file.id).find(".info").find('.btn').fadeOut('slow');//上传完后删除"删除"按钮
        $('#StopBtn').fadeOut('slow');
    });
    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
        //上传出错后进度条变红
        $('#' + file.id).find(".progress").find(".progress-bar").attr("class", "progress-bar progress-bar-danger");
        //添加重试按钮
        //为了防止重复添加重试按钮，做一个判断
        //var retrybutton = $('#' + file.id).find(".btn-retry");
        //$('#' + file.id)
        if ($('#' + file.id).find(".btn-retry").length < 1) {
            var btn = $('<button type="button" fileid="' + file.id + '" class="btn btn-success btn-retry"><span class="glyphicon glyphicon-refresh"></span></button>');
            $('#' + file.id).find(".info").append(btn);//.find(".btn-danger")
        }
        $(".btn-retry").click(function () {
            //console.log($(this).attr("fileId"));//拿到文件id
            uploader.retry(uploader.getFile($(this).attr("fileId")));
        });
    });
    uploader.on('uploadComplete', function (file) {//上传完成后回调
        //$('#' + file.id).find('.progress').fadeOut();//上传完删除进度条
        //$('#' + file.id + 'btn').fadeOut('slow')//上传完后删除"删除"按钮
    });
    uploader.on('uploadFinished', function () {
        //上传完后的回调方法
        //alert("所有文件上传完毕");
        //提交表单
    });
    $("#UploadBtn").click(function () {
        uploader.upload();//上传
    });
    $("#StopBtn").click(function () {
        console.log($('#StopBtn').attr("status"));
        var status = $('#StopBtn').attr("status");
        if (status == "suspend") {
            console.log("当前按钮是暂停，即将变为继续");
            $("#StopBtn").html("继续上传");
            $("#StopBtn").attr("status", "continuous");
            console.log("当前所有文件===" + uploader.getFiles());
            console.log("=============暂停上传==============");
            uploader.stop(true);
            console.log("=============所有当前暂停的文件=============");
            console.log(uploader.getFiles("interrupt"));
        } else {
            console.log("当前按钮是继续，即将变为暂停");
            $("#StopBtn").html("暂停上传");
            $("#StopBtn").attr("status", "suspend");
            console.log("===============所有当前暂停的文件==============");
            console.log(uploader.getFiles("interrupt"));
            uploader.upload(uploader.getFiles("interrupt"));
        }
    });
    uploader.on('uploadAccept', function (file, response) {
        if (response._raw === '{"error":true}') {
            return false;
        }
    });
});
