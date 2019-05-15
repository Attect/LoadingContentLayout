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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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


    //region BlurView

    /**
     * 模糊效果渲染色
     */
    @ColorInt
    var blurBackgroundColor = Color.parseColor("#AAFFFFFF")

    /**
     * 模糊效果起始模糊半径
     */
    var blurRadius = 0f


    /**
     * 模糊效果过度时间
     */
    var blurAnimationTime = 1000

    /**
     * 模糊效果实时采样率降低因子
     */
    var blurDownSampleFactor = 2f

    private var showBlur = false

    //endregion

    private val frameAnimation = FrameAnimation()


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributesSet: AttributeSet) : this(context, attributesSet, 0)

    constructor(context: Context, attributesSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributesSet,
        defStyleAttr
    ) {
        initLoadLayout(attributesSet)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attributesSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attributesSet,
        defStyleAttr,
        defStyleRes
    ) {
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

                val loadingResId = typedArray.getResourceId(R.styleable.LoadingContentLayout_lcl_loading_character, 0)

                loadingCharacterAnimFPS =
                    typedArray.getInt(R.styleable.LoadingContentLayout_lcl_loading_character_fps, 60)

                if (loadingResId > 0) {
                    val loadingCharacterAnimTypedArray = resources.obtainTypedArray(loadingResId)
                    for (i in 0 until loadingCharacterAnimTypedArray.length()) {
                        loadingCharacterDrawableIds.add(loadingCharacterAnimTypedArray.getResourceId(i, -1))
                    }
                    frameAnimation.imageView = loadLayout?.lvlImageView
                    frameAnimation.frameRess = loadingCharacterDrawableIds.toIntArray()
                    frameAnimation.duration = 1000 / loadingCharacterAnimFPS
                    frameAnimation.isRepeat = true
                    loadingCharacterAnimTypedArray.recycle()
                }


                typedArray.getString(R.styleable.LoadingContentLayout_lcl_loading_text)?.let {
                    if (!TextUtils.isEmpty(it)) loadingText = it
                }
                typedArray.getDrawable(R.styleable.LoadingContentLayout_lcl_failed_character)?.let {
                    failedCharacterDrawable = it
                }
                failedCharacterAnimFPS =
                    typedArray.getInt(R.styleable.LoadingContentLayout_lcl_failed_character_fps, 60)
                typedArray.getString(R.styleable.LoadingContentLayout_lcl_failed_text)?.let {
                    if (!TextUtils.isEmpty(it)) failedText = it
                }
                typedArray.getDimension(R.styleable.LoadingContentLayout_lcl_text_size, 0f).let {
                    if (it > 0f) textSize = it
                }
                textColor = typedArray.getColor(R.styleable.LoadingContentLayout_lcl_text_color, Color.BLACK)

                containerBackgroundColor =
                    typedArray.getColor(R.styleable.LoadingContentLayout_lcl_background_color, Color.WHITE)

                //region BlurView
                blurBackgroundColor = typedArray.getColor(
                    R.styleable.LoadingContentLayout_lcl_blur_background_color,
                    Color.parseColor("#AAFFFFFF")
                )
                blurRadius = typedArray.getDimension(R.styleable.LoadingContentLayout_lcl_blur_radius, 0f)
                blurAnimationTime = typedArray.getInt(R.styleable.LoadingContentLayout_lcl_blur_animation_time, 1000)
                blurDownSampleFactor =
                    typedArray.getFloat(R.styleable.LoadingContentLayout_lcl_blur_down_sample_factor, 2f)
                //endregion BlurView
            } catch (e: Exception) {
                e.printStackTrace()
            }


            typedArray.recycle()
        } else return

        //默认隐藏
        loadLayout?.visibility = View.GONE

    }

    private fun applyLoadLayout() {
        if (loadLayout?.parent == null) {
            loadLayout?.setBackgroundColor(containerBackgroundColor)
            addView(loadLayout, childCount) //在最后加上，确保覆盖
            loadLayout?.lvlTextView?.setTextColor(textColor)
            if ((textSize > 0)) lvlTextView.textSize = textSize
            loadLayout?.lvlRealtimeBlurView?.blurRadius = blurRadius
            loadLayout?.lvlRealtimeBlurView?.apply {
                setOverlayColor(blurBackgroundColor)
                setDownSampleFactor(blurDownSampleFactor)
            }
        }

    }

    /**
     * 显示正在加载
     */
    public fun startLoading() {
        post {
            loadLayout?.apply {
                setBackgroundColor(containerBackgroundColor)
                lvlImageView.visibility = View.VISIBLE
                frameAnimation.startPlay()
                lvlTextView?.visibility = View.VISIBLE
                lvlTextView?.text = loadingText
                visibility = View.VISIBLE
            }

        }

    }

    /**
     * 停止显示加载
     * 通常此时也加载成功了
     */
    public fun stopLoading() {
        loadLayout?.lvlImageView?.setImageDrawable(null)
        loadLayout?.lvlTextView?.text = ""
        loadLayout?.visibility = View.GONE
        frameAnimation.release()
    }

    /**
     * 显示加载失败
     */
    public fun showFailed() {
        loadLayout?.apply {
            setBackgroundColor(containerBackgroundColor)
            lvlImageView?.visibility = View.VISIBLE
            lvlImageView?.setImageDrawable(failedCharacterDrawable)
            lvlTextView?.visibility = View.VISIBLE
            lvlTextView?.text = failedText
            visibility = View.VISIBLE
        }

        frameAnimation.release()
    }

    public fun toggleBlur(view: View?) {
        showBlur(!showBlur, view)
    }

    public fun showBlur(show: Boolean, view: View?) {
        loadLayout?.post {
            postShowBlur(show)
        }
        view?.invalidate() //避免部分view动画冻结
    }

    private fun postShowBlur(show: Boolean) {
        showBlur = show
        loadLayout?.apply {
            setBackgroundColor(Color.TRANSPARENT)
            lvlImageView?.visibility = View.GONE
            lvlTextView?.visibility = View.GONE
        }

        if (show) {
            val alphaAnimation = AlphaAnimation(0f, 1f).apply {
                duration = blurAnimationTime.toLong()
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }

                })
            }
            loadLayout?.visibility = View.VISIBLE
            loadLayout?.lvlRealtimeBlurView?.visibility = View.VISIBLE
            loadLayout?.lvlRealtimeBlurView?.startAnimation(alphaAnimation)

        } else {
            val alphaAnimation = AlphaAnimation(1f, 0f).apply {
                duration = blurAnimationTime.toLong()
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        loadLayout?.lvlRealtimeBlurView?.visibility = View.GONE
                        loadLayout?.visibility = View.GONE
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })
            }
            loadLayout?.lvlRealtimeBlurView?.startAnimation(alphaAnimation)

        }
    }


    companion object {
        const val TAG = "LCL"
    }


}