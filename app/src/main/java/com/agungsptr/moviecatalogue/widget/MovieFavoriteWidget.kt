package com.agungsptr.moviecatalogue.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.agungsptr.moviecatalogue.R

/**
 * Implementation of App Widget functionality.
 */
class MovieFavoriteWidget : AppWidgetProvider() {
    companion object {
        private const val TOAST_ACTION = "TOAST_ACTION"
        const val WIDGET_UPDATE = "widget_update"
        const val EXTRA_ITEM = "EXTRA_ITEM"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            when (intent.action) {
                TOAST_ACTION -> {
                    val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0) + 1
                    Toast.makeText(context, "Movie Favorite $viewIndex", Toast.LENGTH_SHORT).show()
                }
                WIDGET_UPDATE -> {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val thisWidget = ComponentName(context, MovieFavoriteWidget::class.java)

                    val appWidgetId = appWidgetManager.getAppWidgetIds(thisWidget)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, StackWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val views = RemoteViews(context.packageName, R.layout.movie_favorite_widget)
        views.setRemoteAdapter(R.id.stack_view, intent)
        views.setEmptyView(R.id.stack_view, R.id.empty_view)

        val toastIntent = Intent(context, MovieFavoriteWidget::class.java)
        toastIntent.action = TOAST_ACTION
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        val toastPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}