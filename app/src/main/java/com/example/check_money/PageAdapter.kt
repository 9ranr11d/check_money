package com.example.check_money


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.check_money.databinding.ItemRecyclerViewBookBinding

class PageAdapter(private var pages: ArrayList<AccountBook>): RecyclerView.Adapter<PageAdapter.ViewHolder>() {
    private val TAG = "PageAdapter"

    //PageAdapter에서 데이터를 상위 클래스로 보내기 위한 인터페이스
    interface OnItemClickListener {
        //클릭 시 작동할 메서드
        fun onClick(target: AccountBook, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    //PageAdapter의 onCreate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_book, parent, false)

        return ViewHolder(ItemRecyclerViewBookBinding.bind(view))
    }

    override fun getItemCount(): Int = pages.size

    //한 줄에 쓸 View 가져오기
    class ViewHolder(val pageAdapterBinding: ItemRecyclerViewBookBinding): RecyclerView.ViewHolder(pageAdapterBinding.root)

    //한 줄의 View마다 줄 값 표시
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(pages[position], position)
        }
        holder.pageAdapterBinding.textViewRecyclerViewSeq.text = pages[position].seq.toString()
        holder.pageAdapterBinding.textViewRecyclerViewDate.text = pages[position].date
        holder.pageAdapterBinding.textViewRecyclerViewAmount.text = pages[position].amount.toString()
        holder.pageAdapterBinding.textViewRecyclerViewContent.text = pages[position].content
    }

    //상위 클래스에서 쓰기 위한 메서드
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    //변경 루틴
    fun routineOfChanging(position: Int, page: AccountBook) {
        MainActivity.makingABook[position] = page
        if(pages[position].mode == "납부") {
            MainActivity.setOfCheckedPeople.remove(pages[position].content)
            MainActivity.setOfCheckedPeople.add(page.content)
        }

        notifyItemChanged(position)
        pages[position] = page
    }

    //삭제 루틴
    fun routineOfRemove(position: Int) {
        MainActivity.makingABook.remove(pages[position])
        if(pages[position].mode == "납부")
            MainActivity.setOfCheckedPeople.remove(pages[position].content)

        notifyItemRemoved(position)
        pages.removeAt(position)
    }
}