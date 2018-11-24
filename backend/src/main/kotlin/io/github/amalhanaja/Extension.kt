package io.github.amalhanaja

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

val utcDateTime: DateTime get() = DateTime.now(DateTimeZone.UTC)

val Long.utcDateTime: DateTime get() = DateTime(this, DateTimeZone.UTC)