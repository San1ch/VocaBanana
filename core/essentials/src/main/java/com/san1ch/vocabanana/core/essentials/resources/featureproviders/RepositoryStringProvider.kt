package com.san1ch.vocabanana.core.essentials.resources.featureproviders

import com.san1ch.vocabanana.core.essentials.resources.StringProvider

interface RepositoryStringProvider : StringProvider {
    val thereIsNoDataByRequest: String
}
