package com.example.informationsecurity.ui.lab4

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator
import com.example.informationsecurity.utils.MD5
import com.example.informationsecurity.utils.OperationState
import com.example.informationsecurity.utils.RC5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.crypto.params.RC5Parameters
import java.io.FileNotFoundException

class Lab4ViewModel(application: Application) : AndroidViewModel(application) {

}
