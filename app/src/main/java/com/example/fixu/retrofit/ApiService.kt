package com.example.fixu.retrofit

import com.example.fixu.database.AddNoteRequest
import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.database.EditNoteRequest
import com.example.fixu.response.DeleteNoteResponse
import com.example.fixu.response.HistoryResponse
import com.example.fixu.response.MLResponse
import com.example.fixu.response.NoteResponse
import com.example.fixu.response.PatchNoteResponse
import com.example.fixu.response.PostNoteResponse
import com.example.fixu.response.QuotesResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/predict/professional/result")
    fun postProfessionalAnswers(
        @Body answers: AnswersProfessional
    ): Call<MLResponse>

    @POST("/predict/student/result")
    fun postStudentAnswers(
        @Body answers: AnswersStudent
    ): Call<MLResponse>

    @GET("/history")
    fun getHistory(): Call<HistoryResponse>

    @GET("/notes")
    fun getNotes(): Call<NoteResponse>

    @POST("/notes/add")
    fun postNote(
        @Body addNoteRequest: AddNoteRequest
    ) : Call<PostNoteResponse>

    @PATCH("/notes/update/{id}")
    fun patchNote(
        @Path("id") id: String,
        @Body editNoteRequest: EditNoteRequest
    ) : Call<PatchNoteResponse>

    @DELETE("notes/{id}")
    fun deleteNote(
        @Path("id") id: String
    ) : Call<DeleteNoteResponse>

    @GET("/quotes/quotes")
    fun getQuotes(): Call<QuotesResponse>
}