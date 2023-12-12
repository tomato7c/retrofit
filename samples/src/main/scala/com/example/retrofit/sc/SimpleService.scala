package com.example.retrofit.sc

import retrofit2.{Call, Retrofit}
import retrofit2.http.{GET, Path}
import retrofit2.converter.gson.GsonConverterFactory
object SimpleService {

  val API_URL = "https://api.github.com"

  case class Contributor(login: String, contributions: Int)

  trait GitHub {
    @GET("/repos/{owner}/{repo}/contributors")
    def contributors(@Path("owner") owner: String, @Path("repo") repo: String): Call[Seq[Contributor]]
  }

  def main(args: Array[String]): Unit = {
    val retrofit = new Retrofit.Builder()
      .baseUrl(API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    val github = retrofit.create(classOf[GitHub])
    val call = github.contributors("square", "retrofit")
    call.execute().body().foreach { it =>
      println(s"${it.login} (${it.contributions})")
    }
  }
}
