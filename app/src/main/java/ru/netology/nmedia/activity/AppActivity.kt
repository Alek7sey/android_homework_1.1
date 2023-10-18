package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import android.Manifest
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()

        intent?.let {
            if (it.action == Intent.ACTION_SEND) {
                val text = it.getStringExtra(Intent.EXTRA_TEXT)
                if (text.isNullOrBlank()) {
                    Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok) {
                            finish()
                        }.show()
                    return@let
                }
                findNavController(R.id.nav_graph).navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
            }
        }

        var currentMenuProvider: MenuProvider? = null
        viewModel.state.observe(this) {
            currentMenuProvider?.let(::removeMenuProvider)

            addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.auth_menu, menu)
                        menu.setGroupVisible(R.id.registered, viewModel.authorized)
                        menu.setGroupVisible(R.id.unregistered, !viewModel.authorized)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                        when (menuItem.itemId) {
//                            R.id.signIn, R.id.signUp -> {
//                                AppAuth.getInstance().setAuth(Token(5L, "x-token"))
//                                true
//                            }
                            R.id.signIn -> {
                                findNavController(R.id.nav_graph).navigate(R.id.action_feedFragment_to_loginFragment)
                                true
                            }

                            R.id.logout -> {
                                appAuth.clear()
                                true
                            }

                            else -> false
                        }
                }.also {
                    currentMenuProvider = it
                }
            )

        }    // checkGoogleApiAvailability()

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermissions(arrayOf(permission), 1)
    }
}


//private fun checkGoogleApiAvailability() {
//    with(GoogleApiAvailability.getInstance()) {
//        val code = isGooglePlayServicesAvailable(this@AppActivity)
//        if (code == ConnectionResult.SUCCESS) {
//            return@with
//        }
//        if (isUserResolvableError(code)) {
//            getErrorDialog(this@AppActivity, code, 9000)?.show()
//            return
//        }
//        Toast.makeText(this@AppActivity, "Google API Unavailable", Toast.LENGTH_SHORT).show()
//    }
//}
