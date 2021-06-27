package com.cyberfanta.desafiobetterfly.models.character

import com.google.gson.annotations.SerializedName

data class Origin3(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class CharacterFilter(

	@field:SerializedName("results")
	val results: List<CharacterDetail?>? = null,

	@field:SerializedName("info")
	val info: Info3? = null
)

data class Info3(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
