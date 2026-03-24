package com.gas.androidtemplate

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gas.androidtemplate.ui.DetailFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DetailFragmentTest {

    @Test
    fun `should show user detail`() {
        // Given
        val arguments = bundleOf("user" to "1 - Leanne Graham")

        // When
        val scenario = launchFragmentInContainer<DetailFragment>(arguments, R.style.Theme_App)

        // Then
        onView(withId(R.id.text)).check(matches(withText("1 - Leanne Graham")))
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun `bad argument key should show empty result`() {
        // Given
        val arguments = bundleOf("id" to "1 - Leanne Graham")

        // When
        val scenario = launchFragmentInContainer<DetailFragment>(arguments, R.style.Theme_App)

        // Then
        onView(withId(R.id.text)).check(matches(withText("")))
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }
}