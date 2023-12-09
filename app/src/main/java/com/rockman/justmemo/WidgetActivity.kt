package com.rockman.justmemo

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.text.Spanned
import android.widget.RemoteViews
import com.rockman.justmemo.utils.MyHtml


class WidgetActivity: AppWidgetProvider() {
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val share = context.applicationContext.getSharedPreferences(
            "JustMemo",
            Context.MODE_PRIVATE
        )
        val content = MyHtml.fromHtml(share.getString("content", "<p>Welcome!</p>"))
        for (i in appWidgetIds.indices) {
            val views = RemoteViews(context.packageName, R.layout.widget)
            val mid = appWidgetIds[i]

            setViewAction(views, mid, context)
            setViewText(views, content)
            appWidgetManager.updateAppWidget(appWidgetIds[i], views)
        }
    }

    private fun setViewAction(views: RemoteViews, mid: Int, context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, mid, intent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(
            R.id.widget_tv,
            pendingIntent
        )
    }

    private fun setViewText(views: RemoteViews, content: Spanned) {
        views.setTextViewText(R.id.widget_tv, content)
    }
}