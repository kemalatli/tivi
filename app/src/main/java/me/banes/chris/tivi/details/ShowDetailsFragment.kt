/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.banes.chris.tivi.details

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_show_details.*
import me.banes.chris.tivi.R
import me.banes.chris.tivi.TiviFragment
import me.banes.chris.tivi.data.entities.TiviShow
import me.banes.chris.tivi.extensions.doWhenLaidOut
import me.banes.chris.tivi.extensions.loadFromUrl
import me.banes.chris.tivi.extensions.observeK
import me.banes.chris.tivi.tmdb.TmdbImageUrlProvider
import javax.inject.Inject

class ShowDetailsFragment : TiviFragment() {

    companion object {
        private const val KEY_SHOW_ID = "show_id"

        fun create(id: Long): ShowDetailsFragment {
            return ShowDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_SHOW_ID, id)
                }
            }
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var imageUrlProvider: TmdbImageUrlProvider

    internal lateinit var viewModel: ShowDetailsFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShowDetailsFragmentViewModel::class.java)

        arguments?.let {
            viewModel.showId = it.getLong(KEY_SHOW_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_show_details, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.observeK(this) {
            it?.let(this::update)
        }
    }

    private fun update(show: TiviShow) {
        details_ctl.title = show.title

        show.tmdbBackdropPath?.let {
            val path = it
            details_backdrop.doWhenLaidOut {
                details_backdrop.loadFromUrl(imageUrlProvider.getBackdropUrl(path, details_backdrop.width))
            }
        }
    }
}