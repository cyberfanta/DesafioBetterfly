package com.cyberfanta.desafiobetterfly.models.character

import com.google.gson.annotations.SerializedName

data class CharacterMultiple(

	@field:SerializedName("CharacterMultiple")
	val characterMultiple: List<CharacterMultipleItem?>? = null
)

data class Location4(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class Origin4(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class CharacterMultipleItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("species")
	val species: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("origin")
	val origin: Origin4? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("location")
	val location: Location4? = null,

	@field:SerializedName("episode")
	val episode: List<String?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
