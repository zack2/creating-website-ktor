package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import freemarker.cache.*
import freemarker.core.HTMLOutputFormat
import io.ktor.freemarker.*
import io.ktor.http.content.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val blogEntry = mutableListOf(BlogEntry(
        "The drive to develop",
        ".. it's what keeps me going."
    ))
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
    }

    routing {
        static("/static") {
            resources("files")
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("entries" to blogEntry), ""))
        }

        post("/submit") {
            val params = call.receiveParameters()
            val headline = params["headline"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val body = params["body"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val newEntry = BlogEntry( headline, body)
            blogEntry.add(0, newEntry)

            call.respondHtml {

                body {
                    h1 {
                        + "Thanks for submitting your entry !"
                    }

                    p {
                        + "We've submitted your entry titled "
                        b { + newEntry.headline }
                    }

                    p { + "You have submitted a total of ${blogEntry.count()} articles !" }

                    a("/"){
                        + "Go back"
                    }
                }
            }
        }

    }
}

data class IndexData(val items: List<Int>)
data class BlogEntry(val headline: String, val body: String)

