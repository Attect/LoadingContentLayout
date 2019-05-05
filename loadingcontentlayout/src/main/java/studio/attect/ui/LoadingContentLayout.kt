package studio.attect.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import kotlinx.android.synthetic.main.load_layout.view.*

/**
 * 可加载内容布局容器
 *
 * @author Attect
 * @date 2019年4月29日
 */
class LoadingContentLayout : FrameLayout {
    private var loadLayout: View? = null

    /**
     * 加载中角色
     */
    private var loadingCharacterDrawableIds = arrayListOf<Int>()

    /**
     * 加载中角色动画
     */
    private var loadingCharacterAnimFPS = 0

    /**
     * 加载中文本
     */
    private var loadingText = ""

    /**
     * 失败角色
     */
    private var failedCharacterDrawable: Drawable = ColorDrawable(Color.TRANSPARENT)

    /**
     * 失败角色动画
     */
    @AnimRes
    private var failedCharacterAnimFPS = 0

    /**
     * 失败文本
     */
    private var failedText = ""

    /**
     * 文本大小
     */
    private var textSize = 0f

    /**
     * 文本颜色
     */
    @ColorInt
    var textColor = Color.BLACK

    @ColorInt
    var containerBackgroundColor = Color.WHITE

    val frameAnimation = FrameAnimation()



    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributesSet: AttributeSet) : this(context, attributesSet, 0)

    constructor(context: Context, attributesSet: AttributeSet, defStyleAttr: Int) : super(context, attributesSet, defStyleAttr) {
        initLoadLayout(attributesSet)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attributesSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributesSet, defStyleAttr, defStyleRes) {
        initLoadLayout(attributesSet)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        applyLoadLayout()
    }


    private fun initLoadLayout(attributesSet: AttributeSet) {
        if (loadLayout == null) {
            loadLayout = LayoutInflater.from(context).inflate(R.layout.load_layout, this, false)

            val typedArray = context.obtainStyledAttributes(attributesSet, R.styleable.LoadingContentLayout)
            try {

                val loadingResId = typedArray.getResourceId(R.styleable.LoadingContentLayout_lcl_loading_character,0)

                loadingCharacterAnimFPS = typedArray.getInt(R.styleable.LoadingContentLayout_lcl_loading_character_fps, 60)

                if(loadingResId > 0){
                    val loadingCharacterAnimTypedArray = resources.obtainTypedArray(loadingResId)
                    for (i in 0 until loadingCharacterAnimTypedArray.length()){
                        loadingCharacterDrawableIds.add(loadingCharacterAnimTypedArray.getResourceId(i,-1))
                    }
                    frameAnimation.imageView = loadLayout?.imageView
                    frameAnimation.frameRess = loadingCharacterDrawableIds.toIntArray()
                    frameAnimation.duration = 1000/loadingCharacterAnimFPS
                    frameAnimation.isRepeat = true
                    loadingCharacterAnimTypedArray.recycle()
                }


                typedArray.getString(R.styleable.LoadingContentLayout_lcl_loading_text)?.let {
                    if (!TextUtils.isEmpty(it)) loadingText = it
                }
                typedArray.getDrawable(R.styleable.LoadingContentLayout_lcl_failed_character)?.let {
                    failedCharacterDrawable = it
                }
                failedCharacterAnimFPS = typedArray.getInt(R.styleable.LoadingContentLayout_lcl_failed_character_fps, 60)
                typedArray.getString(R.styleable.LoadingContentLayout_lcl_failed_text)?.let {
                    if (!TextUtils.isEmpty(it)) failedText = it
                }
                typedArray.getDimension(R.styleable.LoadingContentLayout_lcl_text_size, 0f).let {
                    if (it > 0f) textSize = it
                }
                textColor = typedArray.getColor(R.styleable.LoadingContentLayout_lcl_text_color, Color.BLACK)

                containerBackgroundColor = typedArray.getColor(R.styleable.LoadingContentLayout_lcl_background_color,Color.WHITE)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            typedArray.recycle()
        } else return

        //默认隐藏
        loadLayout?.visibility = View.GONE

    }

    private fun applyLoadLayout(){
        if(loadLayout?.parent == null){
            loadLayout?.setBackgroundColor(containerBackgroundColor)
            addView(loadLayout, childCount) //在最后加上，确保覆盖
            textView.setTextColor(textColor)
            if((textSize > 0)) textView.textSize = textSize

        }

    }

    /**
     * 显示正在加载
     */
    public fun startLoading() {
        post {
            loadLayout?.setBackgroundColor(containerBackgroundColor)
            frameAnimation.startPlay()
            loadLayout?.textView?.text = loadingText
            loadLayout?.visibility = View.VISIBLE
        }

    }

    /**
     * 停止显示加载
     * 通常此时也加载成功了
     */
    public fun stopLoading(){
        loadLayout?.imageView?.setImageDrawable(null)
        loadLayout?.textView?.text = ""
        loadLayout?.visibility = View.GONE
        frameAnimation.release()
    }

    /**
     * 显示加载失败
     */
    public fun showFailed(){
        loadLayout?.setBackgroundColor(containerBackgroundColor)
        loadLayout?.imageView?.setImageDrawable(failedCharacterDrawable)
        loadLayout?.textView?.text = failedText
        loadLayout?.visibility = View.VISIBLE
        frameAnimation.release()
    }


    companion object {
        const val TAG = "LCL"
    }


}