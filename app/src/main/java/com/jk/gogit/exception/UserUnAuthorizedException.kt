package com.jk.gogit.exception

import java.io.IOException

class UserUnAuthorizedException(override var message:String): IOException(message){

}