package com.jinvita.mykakaomap.ui.markerlistview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinvita.mykakaomap.databinding.FragmentListBinding
import com.jinvita.mykakaomap.model.data.CategorySearchData
import com.jinvita.mykakaomap.model.repository.MapRepository
import com.jinvita.mykakaomap.ui.viewmodel.MapViewModel
import com.jinvita.mykakaomap.ui.viewmodel.MapViewModelFactory
import retrofit2.Response

class MarkerListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "KM/MarkerListFragment"
    }

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MarkerListAdapter

    private val mViewModel: MapViewModel by activityViewModels {
        MapViewModelFactory(
            MapRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        addObserver()
    }

    private fun initRecyclerView() {
        mAdapter = MarkerListAdapter(mContext, mViewModel)
        mRecyclerView = binding.markerListView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter
        }
    }

    private fun addObserver() {
        val dataObserver: Observer<Response<CategorySearchData>?> =
            Observer { liveData ->
                // liveData 변경시(api 호출로 데이터 를 가져온 후에, place 정보 recyclerView 추가)
                if (liveData != null && liveData.isSuccessful) {
                    var isEnd = false
                    liveData.body()?.meta?.let {
                        isEnd = it.is_end
                    }
                    Log.i(TAG, "data :" + liveData.body().toString())

                    if (mViewModel.getDataList(mViewModel.getCategoryCode()).isEmpty()) {
                        setRecyclerViewVisibility(false)
                    } else {
                        setRecyclerViewVisibility(true)
                        mAdapter.setDataList(
                            mViewModel.getDataList(mViewModel.getCategoryCode()),
                            isEnd
                        )
                    }
                } else {
                    setRecyclerViewVisibility(false)
                }
            }
        mViewModel.liveData.observe(viewLifecycleOwner, dataObserver)

        mViewModel.liveMarkerItem.observe(viewLifecycleOwner) {
            mAdapter.setSelectItem(it)
            mRecyclerView.smoothScrollToPosition(mAdapter.getSelectedPosition())
        }
    }

    private fun setRecyclerViewVisibility(isShow: Boolean) {
        if (isShow) mRecyclerView.visibility = View.VISIBLE
        else mRecyclerView.visibility = View.GONE
    }
}