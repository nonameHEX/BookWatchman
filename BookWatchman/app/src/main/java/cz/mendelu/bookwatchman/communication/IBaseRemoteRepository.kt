package cz.mendelu.bookwatchman.communication

import android.util.Log
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface IBaseRemoteRepository {
    suspend fun <T : Any> makeApiCall(apiCall: suspend () -> Response<T>): CommunicationResult<T>{
        try {
            val call: Response<T> = apiCall()
            if(call.isSuccessful){
                call.body()?.let {
                    return CommunicationResult.Success(it)
                }?:run {
                    return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
                }
            }
            else{
                return CommunicationResult.Error(CommunicationError(call.code(), call.message()))
            }
        }
        catch (ex: UnknownHostException){
            Log.d("BaseRemoteRepository", "UnknownHostException: ${ex.message}")
            return CommunicationResult.Exception(ex)
        }
        catch (ex: SocketTimeoutException){
            Log.d("BaseRemoteRepository", "SocketTimeoutException: ${ex.message}")
            return CommunicationResult.ConnectionError()
        }
        catch (ex: Exception){
            Log.d("BaseRemoteRepository", "Exception: ${ex.message}")
            return CommunicationResult.Exception(ex)
        }
    }
}