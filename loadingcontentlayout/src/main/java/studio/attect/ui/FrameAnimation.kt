package studio.attect.ui

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView

/**
 * Created by Ansen on 2015/5/14 23:30.
 *
 * @E-mail: ansen360@126.com
 * @Blog: http://blog.csdn.net/qq_25804863
 * @Github: https://github.com/ansen360
 * @PROJECT_NAME: FrameAnimation
 * @PACKAGE_NAME: com.ansen.frameanimation.sample
 * @author Ansen
 * @author Attect
 */
open class FrameAnimation {

    var isRepeat: Boolean = false

    var animationListener: AnimationListener? = null

    var imageView: ImageView? = null

    var frameRess: IntArray? = null
        set(value) {
            if (value != null) {
                lastFrame = value.size
            }
            field = value
        }

    /**
     * 每帧动画的播放间隔数组
     */
    private var durations: IntArray? = null

    /**
     * 每帧动画的播放间隔
     */
    var duration: Int = 0

    /**
     * 下一遍动画播放的延迟时间
     */
    private var delay: Int = 0

    private var lastFrame: Int = 0
        set(value) {
            field = value - 1
        }

    private var next: Boolean = false

    var isPause: Boolean = false
        private set

    private var mCurrentSelect: Int = 0

    private var mCurrentFrame: Int = 0

    constructor() {}

    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    constructor(iv: ImageView, frameRes: IntArray, duration: Int, isRepeat: Boolean) {
        this.imageView = iv
        this.frameRess = frameRes
        this.duration = duration
        this.lastFrame = frameRes.size
        this.isRepeat = isRepeat
    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    constructor(iv: ImageView, frameRess: IntArray, durations: IntArray, isRepeat: Boolean) {
        this.imageView = iv
        this.frameRess = frameRess
        this.durations = durations
        this.lastFrame = frameRess.size
        this.isRepeat = isRepeat
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    constructor(iv: ImageView, frameRess: IntArray, duration: Int, delay: Int) {
        this.imageView = iv
        this.frameRess = frameRess
        this.duration = duration
        this.delay = delay
        this.lastFrame = frameRess.size
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    constructor(iv: ImageView, frameRess: IntArray, durations: IntArray, delay: Int) {
        this.imageView = iv
        this.frameRess = frameRess
        this.durations = durations
        this.delay = delay
        this.lastFrame = frameRess.size
    }

    private fun playByDurationsAndDelay(i: Int) {
        imageView?.postDelayed(Runnable {
            if (isPause) {   // 暂停和播放需求
                mCurrentSelect = SELECTED_A
                mCurrentFrame = i
                return@Runnable
            }
            if (0 == i) {
                animationListener?.onAnimationStart()
            }
            imageView?.setImageResource(frameRess!![i])
            if (i == lastFrame) {
                animationListener?.onAnimationRepeat()
                next = true
                playByDurationsAndDelay(0)
            } else {
                playByDurationsAndDelay(i + 1)
            }
        }, (if (next && delay > 0) delay else durations!![i]).toLong())

    }

    private fun playAndDelay(i: Int) {
        imageView?.postDelayed(Runnable {
            if (isPause) {
                if (isPause) {
                    mCurrentSelect = SELECTED_B
                    mCurrentFrame = i
                    return@Runnable
                }
                return@Runnable
            }
            next = false
            if (0 == i) {
                animationListener?.onAnimationStart()
            }
            imageView?.setImageResource(frameRess!![i])
            if (i == lastFrame) {
                animationListener?.onAnimationRepeat()
                next = true
                playAndDelay(0)
            } else {
                playAndDelay(i + 1)
            }
        }, (if (next && delay > 0) delay else duration).toLong())

    }

    private fun playByDurations(i: Int) {
        imageView?.postDelayed(Runnable {
            if (isPause) {
                if (isPause) {
                    mCurrentSelect = SELECTED_C
                    mCurrentFrame = i
                    return@Runnable
                }
                return@Runnable
            }
            if (0 == i) {
                animationListener?.onAnimationStart()
            }
            imageView?.setImageResource(frameRess!![i])
            if (i == lastFrame) {
                if (isRepeat) {
                    animationListener?.onAnimationRepeat()
                    playByDurations(0)
                } else {
                    animationListener?.onAnimationEnd()
                }
            } else {

                playByDurations(i + 1)
            }
        }, durations!![i].toLong())

    }

    fun  startPlay(){
        frameRess?.let {
            isPause = false
            mCurrentFrame = 0
            lastFrame = it.size
            play(0)
        }

    }

    private fun play(i: Int = 0) {
        frameRess?.let {
            imageView?.postDelayed(Runnable {
                if (isPause) {
                    imageView?.background = null
                    if (isPause) {
                        mCurrentSelect = SELECTED_D
                        mCurrentFrame = i
                        return@Runnable
                    }
                    return@Runnable
                }
                if (0 == i) {
                    animationListener?.onAnimationStart()
                }
                it.let {
                    imageView?.setImageResource(it[i])
                }

                if (i == lastFrame) {

                    if (isRepeat) {
                        animationListener?.onAnimationRepeat()
                        play(0)
                    } else {
                        animationListener?.onAnimationEnd()
                    }

                } else {

                    play(i + 1)
                }
            }, duration.toLong())
        }

    }

    interface AnimationListener {

        /**
         *
         * Notifies the start of the animation.
         */
        fun onAnimationStart()

        /**
         *
         * Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.
         */
        fun onAnimationEnd()

        /**
         *
         * Notifies the repetition of the animation.
         */
        fun onAnimationRepeat()
    }

    fun release() {
        pauseAnimation()
    }

    fun pauseAnimation() {
        this.isPause = true
    }

    fun restartAnimation() {
        if (isPause) {
            isPause = false
            when (mCurrentSelect) {
                SELECTED_A -> playByDurationsAndDelay(mCurrentFrame)
                SELECTED_B -> playAndDelay(mCurrentFrame)
                SELECTED_C -> playByDurations(mCurrentFrame)
                SELECTED_D -> play(mCurrentFrame)
                else -> {
                }
            }
        }
    }

    companion object {

        private val SELECTED_A = 1

        private val SELECTED_B = 2

        private val SELECTED_C = 3

        private val SELECTED_D = 4

        private const val TAG = "FAM"
    }

}
