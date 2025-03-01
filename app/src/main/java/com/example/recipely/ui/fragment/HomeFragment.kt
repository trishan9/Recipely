package com.example.recipely.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.recipely.R
import com.example.recipely.adapter.RecipesAdapter
import com.example.recipely.databinding.FragmentHomeBinding
import com.example.recipely.model.BookmarkModel
import com.example.recipely.model.Recipe
import com.example.recipely.repository.RecipeRepositoryImpl
import com.example.recipely.utils.LoadingUtils
import com.example.recipely.viewmodel.RecipeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by lazy { RecipeViewModel(RecipeRepositoryImpl()) }
    private lateinit var recipeAdapter: RecipesAdapter
    private var currentFilter = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBanner()
        setupRecipesList()
        setupFilterChips()
        loadRecipes()
        setupViewModelObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupBanner() {
        Glide.with(requireContext())
            .load("https://res.cloudinary.com/dnqet3vq1/image/upload/v1740833865/Card_lxxa6l.png")
            .into(binding.bannerImage)

        binding.bannerTitle.text = "Asian white noodle with extra seafood"
        binding.bannerDescription.text = "Exclusive recipe by Chef. Trishan Wagle!"
    }

    private fun setupRecipesList() {
        recipeAdapter = RecipesAdapter(
            emptyList(),
            onRecipeClick = { event -> navigateToRecipeDetail(event) },
            onBookMarkClick = { event -> handleBookmark(event) }
        )

        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter
        }
    }

    private fun setupFilterChips() {
        binding.categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilter = when (checkedId) {
                R.id.chipAll -> "All"
                R.id.chipVeg -> "Vegetarian"
                R.id.chipNonVeg -> "Non-Vegetarian"
                else -> "All"
            }
            filterRecipes()
        }
    }

    private fun loadRecipes() {
        binding.progressBar.visibility = View.VISIBLE
        recipeViewModel.getAllRecipes()
    }

    private fun setupViewModelObservers() {
        recipeViewModel.recipeData.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.updateRecipes(recipes)
            filterRecipes()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun filterRecipes() {
        val allRecipes = recipeAdapter.getAllRecipes()
        val filteredRecipes = if (currentFilter == "All") {
            allRecipes
        } else {
            allRecipes.filter { it.category == currentFilter }
        }
        recipeAdapter.setFilteredRecipes(filteredRecipes)
    }

    private fun navigateToRecipeDetail(recipe: Recipe) {
        Toast.makeText(context, "Viewing ${recipe.title}", Toast.LENGTH_SHORT).show()
    }

    private fun handleBookmark(recipe: Recipe) {
        // Show a simple loader while processing the booking
        val loader = LoadingUtils(requireActivity())
        loader.show()

        // Retrieve the current user ID (assuming FirebaseAuth is set up)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

//        // Create a new booking object; bookingDate is set to the current time.
//        val booking = Booking(
//            eventId = event.id,
//            userId = userId,
//            bookingDate = System.currentTimeMillis(),
//            status = "CONFIRMED"
//        )
//
//        // Send booking data to Firebase via the BookingViewModel
//        bookingViewModel.createBooking(booking) { success, message, bookingId ->
//            loader.dismiss()
//            if (success) {
//                Toast.makeText(context, "Booking successful!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(context, "Booking failed: $message", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}