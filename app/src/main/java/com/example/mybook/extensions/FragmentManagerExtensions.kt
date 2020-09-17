package com.example.mybook.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.replaceFragment(layoutId: Int, nextFragment: Fragment, isAddToBackStack: Boolean) {
    findFragmentByTag(nextFragment::class.java.name) ?: nextFragment.also { fragment ->
        beginTransaction()
            .replace(layoutId, fragment, nextFragment::class.java.name)
            .apply {
                if (isAddToBackStack) {
                    this.addToBackStack(nextFragment::class.java.name)
                }
            }
            .commit()
    }
}