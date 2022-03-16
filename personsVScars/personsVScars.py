# -*- coding: utf-8 -*-
import cv2


def AplicarFiltro(frame, bgSub):
        cv2.imshow("video original", frame);
        
        mask = bgSub.apply(frame)
        cv2.imshow("bgSub frame-sem filtros", mask);
        
        cv2.rectangle(mask, (0, 290), (50, 200), (0,0,0), cv2.FILLED) #para ignorar a arvore
        
        mask = cv2.medianBlur(mask, 5)
        mask = cv2.GaussianBlur(mask, (31,31), 0)
        mask = cv2.threshold(mask, 15, 255, cv2.THRESH_BINARY)[1]
        
        return mask    


path = "video/camera1.mp4"
pathOut = 'ResultVideo.avi'
video = cv2.VideoCapture(path)
    
        
success, frame = video.read() #success diz-me se consegui abrir o ficheiro
#print(frame);
h = frame.shape[0]
w = frame.shape[1]
print(w)
count = 0

#para escrever um output do ficheiro com o texto/formas escritas!
out = cv2.VideoWriter(pathOut, cv2.VideoWriter_fourcc(*"XVID"), 25, (w, h));
out.write(frame);

myPoints = [];  #lista com os meus pontos, indices pares vao ter x, indices impares vao ter y


bgSub = cv2.createBackgroundSubtractorMOG2();

        
while success:
            
            success, frame = video.read() #success diz-me se consegui abrir o ficheiro
            #print(success);
            
            if(success == False): 
                print("Erro ao ler ficheiro!");
                break 
            
            detetou = False;        #flag que serve para saber se na frame atcual(frame1) existe movimento!                    
        
            frame1 = frame.copy();
            
            mask = AplicarFiltro(frame1, bgSub);
            
            contours = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)[0]; 
            numeroContornos =  len(contours);   #usado para desenhar a trajectoria!
            print(numeroContornos)
            
            for c in contours:
                
                A = cv2.contourArea(c)
                
                if(A > 500 and A < 20000):
                    
                    detetou = True
                    
                    Objecto = "Outro"
                    
                    #para conseguir ir escrevendo "Pessoa" ou "Carro" na mesma posição relativamente ao movimento do objeto
                    points = cv2.approxPolyDP(c, 1, True) 
#                    points =  cv2.HoughLines(c,1, 3.14/180,200)
                    x,y,w,h = cv2.boundingRect(points)  #dame jeito um rectangulo para ter a altura e largura
                    
                    if(A < 6000 and h > w):
                        Objecto = "Pessoa"
                        
                    if(A > 1000 and w >= h):
                        Objecto = "Carro"
                    
                    ellipse = cv2.fitEllipse(c)
                    cv2.ellipse(frame1,ellipse,(0,0,255),2)
                    cv2.putText(frame1, Objecto, (x, y-5), cv2.FONT_HERSHEY_TRIPLEX, 0.7, (0,0,255))
                    
# =============================================================================
                M = cv2.moments(c)
                if( M["m10"] > 0 and M["m00"] > 0 and M["m01"] > 0 and  M["m00"] > 0 ):
                    center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
                
                myPoints.append(center);
                
                #print(myPoints[0]);
                #remover os pontos mais antigos
                if(count > 200 and len(myPoints) > 10 ):
                    if(len(myPoints) > 30 * numeroContornos ): #valor que controla o comprimento da cauda, maior é mais longo
                        myPoints.remove(myPoints[0]);
                    
                    #desenhar os pontos mais recentes
                    index = 0;
                    for point in myPoints:
                         if(index >= len(myPoints)):
                             break;
                         # print(index)    
                         cv2.circle(frame1, myPoints[index], 2, (0,0,255), -1);
                         index = index + 1;
                     #cv2.line(frame1, (0,0), (100, 100), (255,0,0), 2 );    
# =============================================================================
            
            
            if(detetou):
                cv2.putText(frame1, "Movimento Detectado!", (30,30), cv2.FONT_HERSHEY_TRIPLEX, 1, (255,0,0))
                
            else:
                cv2.putText(frame1, "Sem Movimento! :(", (30,30), cv2.FONT_HERSHEY_TRIPLEX, 1, (0,255,0)) 
                myPoints.clear();
                
            
            cv2.imshow("Motion Detection", mask)
            
            if cv2.waitKey(19) >= 0:
                cv2.destroyAllWindows()
                cv2.waitKey(1)
                break
              
            out.write(frame1)
            
            count += 1
            
video.release()
out.release()
cv2.destroyAllWindows()
cv2.waitKey(1)       
    






























