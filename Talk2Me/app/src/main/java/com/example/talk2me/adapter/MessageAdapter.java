package com.example.talk2me.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talk2me.R;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.MessageToSend;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MessageAdapter extends ArrayAdapter<MessageToSend> {

    private ImageAdapter.OnItemClickListener mListener;     //usa o interface do ImageAdapter! escuso de escrever outravez aqui
    private Context context;
    private ArrayList<MessageToSend> mensagens;

    public MessageAdapter(Context c, ArrayList<MessageToSend> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está preenchida
        if( mensagens != null ){


            // Recupera dados do usuario remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRementente = preferencias.getIndentifier();

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Recupera mensagem
            MessageToSend mensagem = mensagens.get( position );

            // Monta view a partir do xml
            if(idUsuarioRementente.equals( mensagem.getIdUser() )  ){
                view = inflater.inflate(R.layout.item_message_right, parent, false);     //envio mensagem, fico com a cor violeta a direita
            }else {
                view = inflater.inflate(R.layout.item_message_left, parent, false);     //recebo mensagem, fico com o background branco a esquerda
            }


            // Recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            TextView timeStamp = view.findViewById(R.id.tv_time);

            textoMensagem.setText( mensagem.getMessage() );
            timeStamp.setText(mensagem.getTime());

        }

        return view;

    }


}































































