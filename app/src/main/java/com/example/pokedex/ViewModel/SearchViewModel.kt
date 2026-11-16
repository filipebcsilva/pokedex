import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.Data.api.PokemonService
import com.example.pokedex.Data.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class SearchViewModel : ViewModel() {

    private val service = RetrofitClient.createService(PokemonService::class.java)

    private val _pokemon = MutableLiveData<PokemonEntity?>()
    val pokemon: LiveData<PokemonEntity?> = _pokemon

    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro

    fun limpar() {
        _pokemon.value = null
    }

    fun buscar(nome: String) {
        val call = service.getPokemon(nome.trim().lowercase())

        call.enqueue(object : Callback<PokemonEntity> {
            override fun onResponse(call: Call<PokemonEntity>, response: Response<PokemonEntity>) {
                if (response.isSuccessful) {
                    _pokemon.value = response.body()
                    _erro.value = null
                } else {
                    _pokemon.value = null
                    _erro.value = "Pokémon não encontrado"
                }
            }

            override fun onFailure(call: Call<PokemonEntity>, t: Throwable) {
                _pokemon.value = null
                _erro.value = "Sem conexão"
            }
        })
    }
}

