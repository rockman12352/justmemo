package com.rockman.justmemox

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.rockman.justmemox.utils.MyHtml
import com.rockman.justmemox.utils.SizePicker
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class MainActivity : ComponentActivity() {
    lateinit var editor: EditText
    lateinit var sizePicker: SizePicker
    lateinit var mainLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.main)
        mainLayout = findViewById(R.id.layout_main)

        initEditor()
        initTools()
    }

    private fun initTools() {
       initSizePicker()

        val buttonListener = object: View.OnClickListener{
            override fun onClick(v: View) {
                var start = editor.selectionStart
                var end = editor.selectionEnd
                if (start == end) {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(
                            R.string.tools_hint
                        ), Toast.LENGTH_LONG
                    ).show()
                    return
                }
                if (end < start) {
                    val temp = end
                    end = start
                    start = temp
                }

                // 获取文本
                editor.clearComposingText()
                val spanString = SpannableString(editor.text)

                when (v.id) {
                    R.id.btn_blod -> makeStyleChange(
                        spanString, start, end,
                        StyleSpan(Typeface.BOLD)
                    )

                    R.id.btn_ital -> makeStyleChange(
                        spanString, start, end,
                        StyleSpan(Typeface.ITALIC)
                    )

                    R.id.btn_under_decorect -> makeStyleChange(
                        spanString, start, end,
                        UnderlineSpan()
                    )

                    R.id.btn_size -> {
                        if (sizePicker.isLoaded) {
                            mainLayout.removeView(sizePicker);
                            sizePicker.isLoaded = false;
                        } else {
                            mainLayout.addView(sizePicker);
                            sizePicker.isLoaded = true;
                        }
                    }

                    R.id.btn_color -> {
                        var nowColor: Int = Color.BLACK
                        val foreSpans: Array<ForegroundColorSpan> = spanString.getSpans(
                            start,
                            end, ForegroundColorSpan::class.java
                        )
                        if (foreSpans.size >= 1) {
                            nowColor = foreSpans[foreSpans.size - 1]
                                .foregroundColor
                        }
                        val builder = ColorPickerDialog.Builder(this@MainActivity, R.style.Theme_Justmemo)
                            .setTitle("ColorPicker Dialog")
                            .setPositiveButton("OK", object: ColorEnvelopeListener{
                                override fun onColorSelected(
                                    envelope: ColorEnvelope,
                                    fromUser: Boolean
                                ) {
                                    makeStyleChange(
                                        spanString,
                                        start, end, ForegroundColorSpan(
                                            envelope.color
                                        )
                                    )
                                    editor.setText(spanString)
                                    editor.setSelection(start, end)
                                }

                            }).setNegativeButton("NO", object: OnClickListener{
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    dialog.dismiss()
                                }

                            }).attachAlphaSlideBar(true).attachBrightnessSlideBar(true).setBottomSpace(12)
                        val view = builder.colorPickerView
                        view.setInitialColor(nowColor)
                        builder.colorPickerView = view
                        builder.show()
                    }
                }
                editor.setText(spanString)
                editor.setSelection(start, end);
            }

        }
        listOf(R.id.btn_blod, R.id.btn_color, R.id.btn_ital, R.id.btn_size, R.id.btn_under_decorect).forEach {
            findViewById<Button>(it).setOnClickListener(buttonListener)
        }

        findViewById<Button>(R.id.btn_ok).setOnClickListener {
            val share = getSharedPreferences("JustMemo", MODE_PRIVATE)
            editor.clearComposingText()
            share.edit().putString("content", MyHtml.toHtml(editor.text)).apply()

            val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)
            val views = RemoteViews(packageName, R.layout.widget)
            views.setTextViewText(R.id.widget_tv, editor.text)

            val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this.applicationContext, WidgetActivity::class.java))
            appWidgetManager.updateAppWidget(widgetIds, views)
            finish()
        }
        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }

    private fun initSizePicker() {
        sizePicker = SizePicker(this, findViewById(R.id.layout_tools))
        val sizePickerOnClickListener = object :View.OnClickListener{
            override fun onClick(v: View) {
                val text_size = (v as Button).textSize
                val absoluteSizeSpan = AbsoluteSizeSpan(text_size.toInt())
                var start: Int = editor.getSelectionStart()
                var end: Int = editor.getSelectionEnd()
                if (start == end) {
                    return
                }
                if (end < start) {
                    val temp = end
                    end = start
                    start = temp
                }
                var spanString = SpannableString(editor.getText())
                spanString = makeStyleChange(
                    spanString, start, end,
                    absoluteSizeSpan
                )
                editor.setText(spanString)
                editor.setSelection(start, end)
                if (sizePicker.isLoaded) {
                    mainLayout.removeView(sizePicker)
                    sizePicker.isLoaded = false
                }
            }
        }
        sizePicker.b1.setOnClickListener(sizePickerOnClickListener)
        sizePicker.b2.setOnClickListener(sizePickerOnClickListener)
        sizePicker.b3.setOnClickListener(sizePickerOnClickListener)
        sizePicker.b4.setOnClickListener(sizePickerOnClickListener)
        sizePicker.b5.setOnClickListener(sizePickerOnClickListener)
        sizePicker.b6.setOnClickListener(sizePickerOnClickListener)
    }

    private fun initEditor() {
        editor = findViewById(R.id.editor)
        val share = getSharedPreferences("JustMemo", MODE_PRIVATE)
        editor.setText(MyHtml.fromHtml(share.getString("content", "<p>Welcome!</p>")))
        if (editor.text.lastIndex!=-1) {
            editor.text.delete(
                editor.text.lastIndex,
                editor.text.lastIndex + 1
            ) // remove trailing space
        }
    }

    private fun makeStyleChange(
        spannableString: SpannableString,
        start: Int, end: Int, style: StyleSpan
    ){
        var flag = true
        // 检查是否已经有所在属性
        val styleSpans = spannableString.getSpans(
            start, end,
            style.javaClass
        )
        for (styleSpan in styleSpans) {
            if (styleSpan.style == style.style) {
                flag = false
                break
            }
        }
        dealOtherSpanForStyleSpan(
            spannableString,
            style, start, end
        )
        if (flag) {
            spannableString.setSpan(
                style, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun makeStyleChange(
        spannableString: SpannableString,
        start: Int, end: Int, style: UnderlineSpan
    ){
        var flag = true
        // 检查是否已经有所在属性
        val styleSpans = spannableString.getSpans(
            start, end,
            UnderlineSpan::class.java
        )
        for (styleSpan in styleSpans) {
            flag = false
            break
        }
        dealOtherSpan(spannableString, style, start, end)
        if (flag) {
            spannableString.setSpan(
                style, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun makeStyleChange(
        spannableString: SpannableString,
        start: Int, end: Int, style: ForegroundColorSpan
    ): SpannableString {
        // 检查是否已经有所在属性
        dealOtherSpan(spannableString, style, start, end)
        spannableString.setSpan(
            style, start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun makeStyleChange(
        spannableString: SpannableString,
        start: Int, end: Int, style: AbsoluteSizeSpan
    ): SpannableString {
        dealOtherSpan(spannableString, style, start, end)
        spannableString.setSpan(
            style, start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    /**
     * 把多余的SPAN处理掉，然后返回一个选择区域内干净的SPAN
     *
     * @param spannableString
     * @param style
     * @param start
     * @param end
     * @return
     */
    private fun dealOtherSpan(
        spannableString: SpannableString,
        style: Any, start: Int, end: Int
    ){
        val styleSpans = spannableString.getSpans(
            start, end,
            style.javaClass
        )
        for (styleSpan in styleSpans) {
            val mstart = spannableString.getSpanStart(styleSpan)
            val mend = spannableString.getSpanEnd(styleSpan)
            spannableString.removeSpan(styleSpan)
            // 原SPAN有局部在现SPAN左边
            if (mstart < start && mend > start && mend < end) {
                spannableString.setSpan(
                    styleSpan, mstart, start,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (end < mend && end > mstart && mstart > start) {
                spannableString.setSpan(
                    styleSpan, end, mend,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (mstart <= start && mend >= end) {
                if (mstart != start) {
                    spannableString.setSpan(
                        styleSpan, mstart, start,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (mend != end) {
                    spannableString.setSpan(
                        secondStyle(styleSpan), end, mend,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    /**
     * stylespan比较特殊，一个类里面带两种值，所以分开处理
     *
     * @param spannableString
     * @param style
     * @param start
     * @param end
     * @return
     */
    private fun dealOtherSpanForStyleSpan(
        spannableString: SpannableString, style: StyleSpan, start: Int, end: Int
    ): SpannableString {
        val bold_or_ital = style.spanTypeId
        val styleSpans = spannableString.getSpans(
            start, end,
            style.javaClass
        )
        for (styleSpan in styleSpans) {
            val mstart = spannableString.getSpanStart(styleSpan)
            val mend = spannableString.getSpanEnd(styleSpan)
            if (bold_or_ital != styleSpan.spanTypeId) {
                continue
            }
            spannableString.removeSpan(styleSpan)
            // 原SPAN有局部在现SPAN左边
            if (mstart < start && mend > start && mend < end) {
                spannableString.setSpan(
                    styleSpan, mstart, start,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (end < mend && end > mstart && mstart > start) {
                spannableString.setSpan(
                    styleSpan, end, mend,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (mstart <= start && mend >= end) {
                if (mstart != start) {
                    spannableString.setSpan(
                        styleSpan, mstart, start,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (mend != end) {
                    spannableString.setSpan(
                        secondStyle(styleSpan), end, mend,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        return spannableString
    }

    private fun secondStyle(ordinary: Any): Any? {
        return if (AbsoluteSizeSpan::class.java.isInstance(ordinary)) {
            AbsoluteSizeSpan((ordinary as AbsoluteSizeSpan).size)
        } else if (ForegroundColorSpan::class.java.isInstance(ordinary)) {
            ForegroundColorSpan(
                (ordinary as ForegroundColorSpan).foregroundColor
            )
        } else if (StyleSpan::class.java.isInstance(ordinary)) {
            StyleSpan((ordinary as StyleSpan).style)
        } else if (UnderlineSpan::class.java.isInstance(ordinary)) {
            UnderlineSpan()
        } else {
            null
        }
    }
}