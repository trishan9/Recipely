package com.example.recipely.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipely.R
import com.example.recipely.adapter.MyRecipesAdapter
import com.example.recipely.databinding.FragmentRecipesBinding
import com.example.recipely.model.Recipe
import com.example.recipely.repository.RecipeRepositoryImpl
import com.example.recipely.repository.UserRepositoryImpl
import com.example.recipely.ui.activity.AddRecipeActivity
import com.example.recipely.viewmodel.RecipeViewModel
import com.example.recipely.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private val recipeViewModel: RecipeViewModel by lazy {
        RecipeViewModel(RecipeRepositoryImpl())
    }
    private lateinit var myRecipesAdapter: MyRecipesAdapter
    private var currentFilter: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.visibility = View.VISIBLE

        setupFab()
        setupRecyclerView()
        setupFilterChips()
        fetchUserRecipes()
    }

    private fun setupRecyclerView() {
        myRecipesAdapter = MyRecipesAdapter(
            recipeList = mutableListOf(),
            onEditClicked = { recipe -> launchUpdateRecipeActivity(recipe) },
            onDeleteClicked = { recipe -> deleteRecipe(recipe) }
        )

        binding.recyclerViewMyRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myRecipesAdapter
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            Log.d("MyRecipesFragment", "FAB clicked! Opening AddRecipeActivity")
            startActivity(Intent(requireContext(), AddRecipeActivity::class.java))
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            currentFilter = when (checkedId) {
                R.id.chipAll -> "All"
                R.id.chipVeg -> "Vegetarian"
                R.id.chipNonVeg -> "Non-Vegetarian"
                else -> "All"
            }
            filterRecipes()
        }
    }

    private fun filterRecipes() {
        val filteredRecipes = if (currentFilter == "All") {
            myRecipesAdapter.getAllRecipes()
        } else {
            myRecipesAdapter.getAllRecipes().filter { it.category == currentFilter }
        }
        myRecipesAdapter.setData(filteredRecipes)
        updateEmptyState(filteredRecipes.isEmpty())
    }

    private fun launchUpdateRecipeActivity(
        recipe: Recipe
    ) {
//        val intent = Intent(requireContext(), UpdateRecipeActivity::class.java).apply {
//            putExtra("EVENT_ID", event.id)
//            putExtra("EVENT_TITLE", event.title)
//            putExtra("EVENT_DESCRIPTION", event.description)
//            putExtra("EVENT_DATE", event.date)
//            putExtra("EVENT_LOCATION", event.location)
//            putExtra("EVENT_CATEGORY", event.category)
//            putExtra("EVENT_PRICE", event.price)
//            putExtra("EVENT_CAPACITY", event.capacity)
//            putExtra("EVENT_IMAGE_URL", event.imageUrl)
//        }
//        startActivity(intent)
    }

    private fun deleteRecipe(recipe: Recipe) {
        recipeViewModel.deleteRecipe(recipe.id) { success, message ->
            if (success) {
                Snackbar.make(binding.myRecipesRoot, "Recipe deleted", Snackbar.LENGTH_SHORT).show()
                fetchUserRecipes()
            } else {
                Snackbar.make(binding.myRecipesRoot, "Error: $message", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUserRecipes()
    }

    private fun fetchUserRecipes() {
        val currentUser = userViewModel.getCurrentUser()
        if (currentUser == null) {
            Snackbar.make(binding.myRecipesRoot, "User not logged in", Snackbar.LENGTH_SHORT).show()
            return
        }

        recipeViewModel.getRecipesByUser(currentUser.uid) { recipes: List<Recipe>, success: Boolean, message: String ->
            if (success) {
                myRecipesAdapter.setAllRecipes(recipes)
                filterRecipes()
            } else {
                Snackbar.make(binding.myRecipesRoot, "Error: $message", Snackbar.LENGTH_SHORT)
                    .show()
                updateEmptyState(true)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewMyRecipes.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMyRecipes.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}