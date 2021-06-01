package com.cyberfanta.desafiobetterfly.models.location

import com.google.gson.annotations.SerializedName

data class LocationMultipleItem(

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("residents")
	val residents: List<String?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("dimension")
	val dimension: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class LocationMultiple(

	@field:SerializedName("locationMultiple")
	val locationMultiple: List<LocationMultipleItem?>? = null
)
