package be.helmo.myscout.phaseeditor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import be.helmo.myscout.R
import be.helmo.myscout.model.Phase
import java.util.*

class PhaseFragment : Fragment() {
    private var mViewModel: PhaseViewModel? = null
    private var namePhaseEditText: EditText? = null
    private var phaseId: UUID? = null
    private var phase: Phase? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.phase_fragment, container, false)
        /*if(savedInstanceState != null) {
            phaseId = (UUID) savedInstanceState.getSerializable(PHASE_ID_ARG);
        }*/
        Log.d(TAG, "onCreateView called")
        namePhaseEditText = view.findViewById<EditText>(R.id.phase_name)
        mViewModel = ViewModelProvider(requireActivity()).get(PhaseViewModel::class.java)
        mViewModel!!.loadPhase(phaseId)?.observe(
            viewLifecycleOwner,
            Observer<Any?> { phase ->
                this@PhaseFragment.phase = phase as Phase?
                updateUi()
            }
        )
        return view
    }

    private fun updateUi() {
        namePhaseEditText?.setText(phase?.name)
    }

    override fun onStart() {
        super.onStart()
        namePhaseEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                phase?.name = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onStop() {
        super.onStop()
        mViewModel?.savePhase(phase)
    }

    companion object {
        private const val TAG = "PhaseFragment"
        private const val PHASE_ID_ARG = "be.helmo.myscout.phaseeditor.PhaseFragment.PHASE_ID_ARG"

        fun newInstance(phaseId: UUID?): PhaseFragment {
            val phaseFragment = PhaseFragment()
            phaseFragment.phaseId = phaseId
            /*Bundle bundle = new Bundle();
        bundle.putSerializable(PHASE_ID_ARG, phaseId);
        phaseFragment.setArguments(bundle);*/
            return phaseFragment
        }
    }
}