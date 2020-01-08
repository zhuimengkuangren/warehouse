package com.npi.warehouse

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.npi.warehouse.fileutil.BitmapUtils
import com.npi.warehouse.fileutil.FileMgr
import com.npi.warehouse.fileutil.ImageUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_camera.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.IOException

/**
 *  author : zougf
 *  time   : 2020/01/08
 *  Copyright(c) 2018 极光
 *  desc   :
 */
class CameraActivity : AppCompatActivity() {
    private lateinit var tempFile: String
    private val INTENT_CAMERA = 100
    private val INTENT_PHOTO = 200
    private val INTENT_CUT = 300
    private lateinit var rxPermission: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        rxPermission = RxPermissions(this)
        tempFile = FileMgr.getFilePath(this) + "photo.jpg"

        bt_select_photo.setOnClickListener {
            selectPhoto()
        }
        bt_take_photo.setOnClickListener {
            takePhoto()
        }
    }

    @SuppressLint("CheckResult")
    private fun selectPhoto() {
        rxPermission.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                ?.subscribe { granted ->
                    if (granted) {
                        val it = ImageUtils.getPhotoIntent()
                        startActivityForResult(it, INTENT_PHOTO)
                    } else {
                        Toast.makeText(this, "缺少读写存储权限", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    @SuppressLint("CheckResult")
    private fun takePhoto() {
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                ?.subscribe { granted ->
                    if (granted) {
                        val outputImage = File(tempFile)
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete()
                            }
                            outputImage.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        val it = ImageUtils.takeCamera(this, tempFile)
                        startActivityForResult(it, INTENT_CAMERA)
                    } else {
                        Toast.makeText(this, "缺少拍照和SD卡读写权限", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        val uri: Uri?
        val intent: Intent
        when (requestCode) {
            INTENT_CAMERA -> {
                uri = ImageUtils.getFileUri(this, File(tempFile))
                intent = ImageUtils.takeCut(uri, tempFile, 1, 1, 500, 500)
                startActivityForResult(intent, INTENT_CUT)
            }
            INTENT_PHOTO -> {
                uri = data?.data
                intent = ImageUtils.takeCut(uri, tempFile, 1, 1, 500, 500)
                startActivityForResult(intent, INTENT_CUT)
            }
            INTENT_CUT -> {//进行图片裁切（不需要就删除这部分逻辑）
                compress(tempFile)
            }
        }
    }

    private fun compress(tempFile: String) {
        //使用了鲁班压缩
        Luban.with(this)
                .load(tempFile)
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                    }

                    override fun onSuccess(file: File) {
                        val bytes = BitmapUtils.encodeBase64Bitmap(file.absolutePath).toTypedArray()
                        //TODO 得到字节码数组，进行上传服务器或者其它处理
                    }

                    override fun onError(e: Throwable) {
                        throw IllegalArgumentException("图片处理错误")
                    }
                })
                .launch()
    }
}