package com.sparktech.studyzoneadmin.user

/*
class UserNotActivatedTabFragment : Fragment() {

    private lateinit var binding: UsersFragmentLayoutBinding
    private lateinit var vm: UsersViewModel
    private lateinit var adapter: UsersAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.users_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        adapter = UsersAdapter()
        binding.userRcv.adapter = adapter
        binding.userRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.userRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchUsers
            )
        )
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        if (vm.currentNotActivatedUsersPage == 0) {
            vm.fetchUsers(adminId!!, "NOT_ACTIVATED", vm.currentNotActivatedUsersPage)
        }
        setObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.isLoadingNotActivatedUsers.value!!
    }
    private val getListSize: () -> Int = {
        vm.notActivatedUsers.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem = User(
            "", "", "", "", "", "",
            false, false, 0, 0, 0,
            false, isLoading = true
        )
        vm.notActivatedUsers.add(loaderItem)
        adapter.notifyItemInserted(vm.notActivatedUsers.size - 1)
    }
    private val fetchUsers = {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val adminId = sp.getString("adminId", "")
        vm.fetchUsers(adminId!!, "NOT_ACTIVATED", vm.currentNotActivatedUsersPage)
    }

    private fun setObservers() {
        vm.isLoadingNotActivatedUsers.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.userLoader.visibility = View.GONE
                    if (vm.notActivatedUsers.size > 0) {
                        binding.noUserData.visibility = View.GONE
                        binding.userRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.notActivatedUsers)
                        adapter.notifyDataSetChanged()
                        return@let
                    }
                    binding.noUserData.visibility = View.VISIBLE
                } else {
                    if (vm.notActivatedUsers.size == 0) {
                        binding.userLoader.visibility = View.VISIBLE
                        binding.userRcv.visibility = View.GONE
                        binding.noUserData.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun initRcv() {
        adapter.submitList(vm.users)
    }
}*/