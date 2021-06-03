package com.cyberfanta.desafiobetterfly.models.character

import com.google.gson.annotations.SerializedName

//data class Origin1(
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("url")
//	val url: String? = null
//)

data class Character(

	@field:SerializedName("results")
	val results: List<CharacterDetail?>? = null,

	@field:SerializedName("info")
	val info: Info1? = null
)

//data class ResultsItem1(
//
//	@field:SerializedName("image")
//	val image: String? = null,
//
//	@field:SerializedName("gender")
//	val gender: String? = null,
//
//	@field:SerializedName("species")
//	val species: String? = null,
//
//	@field:SerializedName("created")
//	val created: String? = null,
//
//	@field:SerializedName("origin")
//	val origin: Origin1? = null,
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("location")
//	val location: Location1? = null,
//
//	@field:SerializedName("episode")
//	val episode: List<String?>? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null,
//
//	@field:SerializedName("type")
//	val type: String? = null,
//
//	@field:SerializedName("url")
//	val url: String? = null,
//
//	@field:SerializedName("status")
//	val status: String? = null
//)
//
//data class Location1(
//
//	@field:SerializedName("name")
//	val name: String? = null,
//
//	@field:SerializedName("url")
//	val url: String? = null
//)

data class Info1(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
