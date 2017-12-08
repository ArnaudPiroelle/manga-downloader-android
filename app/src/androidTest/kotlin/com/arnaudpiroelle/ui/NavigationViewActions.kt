package com.arnaudpiroelle.ui

import android.content.res.Resources.NotFoundException
import android.support.design.widget.NavigationView
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.util.HumanReadables
import android.view.Menu
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object NavigationViewActions {

    fun navigateTo(menuItemId: Int): ViewAction {

        return object : ViewAction {

            override fun perform(uiController: UiController, view: View) {
                val navigationView = view as NavigationView
                val menu = navigationView.menu
                if (null == menu.findItem(menuItemId)) {
                    throw PerformException.Builder().withActionDescription(this.description).withViewDescription(HumanReadables.describe(view)).withCause(RuntimeException(getErrorMessage(menu, view))).build()
                }
                menu.performIdentifierAction(menuItemId, 0)
                uiController.loopMainThreadUntilIdle()
            }

            private fun getErrorMessage(menu: Menu, view: View): String {
                val NEW_LINE = System.getProperty("line.separator")
                val errorMessage = StringBuilder("Menu item was not found, " + "available menu items:").append(NEW_LINE)
                for (position in 0..menu.size() - 1) {
                    errorMessage.append("[MenuItem] position=").append(position)
                    val menuItem = menu.getItem(position)
                    if (menuItem != null) {
                        val itemTitle = menuItem.title
                        if (itemTitle != null) {
                            errorMessage.append(", title=").append(itemTitle)
                        }
                        if (view.resources != null) {
                            val itemId = menuItem.itemId
                            try {
                                errorMessage.append(", id=")
                                val menuItemResourceName = view.resources.getResourceName(itemId)
                                errorMessage.append(menuItemResourceName)
                            } catch (nfe: NotFoundException) {
                                errorMessage.append("not found")
                            }

                        }
                        errorMessage.append(NEW_LINE)
                    }
                }
                return errorMessage.toString()
            }

            override fun getDescription(): String {
                return "click on menu item with id"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(isAssignableFrom(NavigationView::class.java),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                        isDisplayingAtLeast(90))
            }
        }

    }
}
