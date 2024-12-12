package com.example.fixu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fixu.DiagnoseActivity
import com.example.fixu.R
import com.example.fixu.database.AppDatabase
import com.example.fixu.database.Question
import com.example.fixu.databinding.FragmentDiagnoseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(requireContext())
        insertInitialQuestions()

        binding.btnProfessional.setOnClickListener {
            navigateToDiagnoseActivity("Professional")
        }

        binding.btnStudent.setOnClickListener {
            navigateToDiagnoseActivity("Student")
        }
    }

    private fun navigateToDiagnoseActivity(userStatus: String) {
        val intent = Intent(requireContext(), DiagnoseActivity::class.java).apply {
            putExtra("USER_STATUS", userStatus)
        }
        startActivity(intent)
    }


    private fun insertInitialQuestions() {
        val questions = listOf(
            Question(
                status = "Professional",
                questionText = "What is your gender?",
                answerType = "radio",
                options = listOf("Male", "Female")
            ),
            Question(
                status = "Professional",
                questionText = "How old are you?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Professional",
                questionText = "How would you rate your work-related stress on a scale of 1 to 5?\n1: my job easy peasy!\n2: my job was kinda fine\n3: one day fun, others stressed\n4: it's a big pressure for me\n5: it's killing me!",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "How satisfied are you with your job, from 1 to 5?\n1: i wanna quit.\n2: i kinda hate this job :(\n3: it's a love-hate relationship with it\n4: i kinda love this job\n5: i love my job till my bone",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "How many hours do you sleep each night?",
                answerType = "radio",
                options = listOf("Less than 5 hours", "5-6 hours", "7-8 hours", " More than 8 hours")
            ),
            Question(
                status = "Professional",
                questionText = "How would you describe your eating habits?",
                answerType = "radio",
                options = listOf("Healthy", "Moderate", "Unhealthy")
            ),
            Question(
                status = "Professional",
                questionText = "Have you ever had thoughts about self-harm or suicide?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Professional",
                questionText = "On average, how many hours do you work each day?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Professional",
                questionText = "How would you rate your financial stress level on a scale of 1 to 5?\n1. Im Rich.\n2. I rarely struggle with it\n3. its all about up and down\n4. i mostly feel that financial stress\n5. im stressful cz im broke",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "Is there a family history of mental health issues?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Student",
                questionText = "What is your gender?",
                answerType = "radio",
                options = listOf("Male", "Female")
            ),
            Question(
                status = "Student",
                questionText = "How old are you?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Student",
                questionText = "How would you rate your academic pressure on a scale of 1 to 5?\n" +
                        "1: my education easy peasy!\n" +
                        "2: my education was kinda fine\n" +
                        "3: one day fun, others stressed\n" +
                        "4: it's a big pressure for me\n" +
                        "5: it's killing me!",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "How satisfied are you with your studies, from 1 to 5?\n1: i wanna drop out.\n2: i kinda hate this education :(\n3: it's a love-hate relationship with it\n4: i kinda love this education\n5: i love this education till my DNA",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "How many hours do you sleep each night?",
                answerType = "radio",
                options = listOf("Less than 5 hours", "5-6 hours", "7-8 hours", " More than 8 hours")
            ),
            Question(
                status = "Student",
                questionText = "How would you describe your eating habits?",
                answerType = "radio",
                options = listOf("Healthy", "Moderate", "Unhealthy")
            ),
            Question(
                status = "Student",
                questionText = "Have you ever had thoughts about self-harm or suicide?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Student",
                questionText = "On average, how many hours do you study each day?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Student",
                questionText = "How would you rate your financial stress level on a scale of 1 to 5?\n1. Im Rich.\n2. I rarely struggle with it\n3. its all about up and down\n4. i mostly feel that financial stress\n5. im stressful cz im broke",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "Is there a family history of mental health issues?",
                answerType = "radio",
                options = listOf("Yes", "No")
            )
        )

        lifecycleScope.launch {
            val existingQuestions = database.questionDao().getQuestionsByStatus("Professional") +
                    database.questionDao().getQuestionsByStatus("Student")

            if (existingQuestions.isEmpty()) {
                withContext(Dispatchers.IO) {
                    database.questionDao().insertQuestions(questions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
