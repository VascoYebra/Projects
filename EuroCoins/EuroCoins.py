# -*- coding: utf-8 -*-
"""
Created on Tue Oct 13 11:32:33 2020

@author: Yebroni
"""
import numpy as np
import cv2
import matplotlib.pyplot as plt


fileDir = "img/"
fileName = "img3.jpg"
#funciona em todas

src = cv2.imread(fileDir+fileName)
#srcGray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY)
srcGray = src[:,:,2]          # 0 e blue, 1 e greeen, 2 e red 
thres, bw = cv2.threshold(srcGray, 150, 250, cv2.THRESH_OTSU)
print(thres)

cv2.imshow('1-Original', src)
#cv2.imshow('2-Gray Image', srcGray)
#cv2.imshow('3-Binary', bw)

#ver o hist da figura para saber o threshold
nbins = 256
v_range = [0, 255]
hist = cv2.calcHist( [srcGray], [0], None, [nbins], v_range )
ind = np.arange(nbins)
width = 0.35
p1 = plt.bar(ind, np.squeeze(hist), width, color='r')
plt.show()

# Melhoramento da imagem (4)
strElem = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (10,10)) #aqui é 10,10 para separar os contornos mais juntos
# bw1 = cv2.morphologyEx(bw, cv2.MORPH_DILATE , strElem)
bw1 = cv2.erode(bw, strElem)
#cv2.imshow('4-Enchanched', bw1)

# regionNum, lb = cv2.connectedComponents(bw1)
# cv2.imshow('label bw', np.uint8(lb*np.round(255.0/regionNum)))

# colorM = psColor.CreateColorMap(regionNum, 1)
# pseudoC = psColor.Gray2PseudoColor(lb,colorM)

# cv2.imshow('label image', pseudoC)


#desenhar os contornos da moeda!
contours, hierarchy = cv2.findContours(bw1, cv2.RETR_CCOMP , cv2.CHAIN_APPROX_SIMPLE)  #RETR_CCOMP para ter a hierarquia
img_contours = np.zeros(src.shape)
cv2.drawContours(img_contours, contours, -1, (128,0,0), 1)
#cv2.imshow('contornos!', img_contours ) 

# print(type(hierarchy))
print(hierarchy) 

#Filtrar o objecto vermelho. So ponho na lista contornos válidos que não tenham filhos !
i = 0
validContors = []
for x in hierarchy:
    while(i != (len(x)) ):
        #este if em baixo é no caso de o contorno ter 0 filhos!
        if( x[i,2] == -1):
            #so adiciono a lista se nao tiver filho! (so a posicao[2] tiver a -1)
            validContors.append(contours[i])        
        #neste caso, o contorno tem filhos, mas se tiver 2 ou mais SEGUIDOS, ja o posso voltar a por dentro da lista   
        elif ( (i+2<(len(x)-1)) and x[i+1,3] == x[i+2,3] ):
            #se, dois contornos a frente deste, tiverem o mesmo pai, então tambem adiciono
            validContors.append(contours[i])
        i = i + 1
        
        
#agora vou detectar as diferentes FORMAS pela área
total = 0        
for cnt in validContors:
    area = cv2.contourArea(cnt)
    if area > 1:
        #continue
        M = cv2.moments(cnt)
        if M != None:
            #calculo dos centroides!
            cX = int(M["m10"] / M["m00"])
            cY = int(M["m01"] / M["m00"])
            	# draw the contour and center of the shape on the image
            # cv2.drawContours(img_contours, [cnt], -1, (0, 255, 0), 2)
            # cv2.circle(img_contours, (cX, cY), 7, (255, 255, 255), -1)
            areaCurrentCnt = cv2.contourArea(cnt)
            # print(areaCurrentCnt)
            #antes de testar preciso de verificar que o contorno é circular! 
            
            #agora vou filtrar os contornos por circularidade!
            perimeter = cv2.arcLength(cnt, True)
            area = cv2.contourArea(cnt)
            circularity = 4*np.pi*(area/(perimeter*perimeter))
            # circularity = (perimeter*perimeter)/area          ## como o professor deu na aula!
            # print (circularity)
            if 0.6 < circularity < 1.2:
                    areaCurrentCnt = cv2.contourArea(cnt)
                    # print(areaCurrentCnt)
                    if areaCurrentCnt > 19000 and areaCurrentCnt < 21000:
                        cv2.putText(src, "50 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)  
                        total += 0.50
                    elif areaCurrentCnt > 12500 and areaCurrentCnt < 13500:
                        cv2.putText(src, "10 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)  
                        total += 0.10
                    elif areaCurrentCnt > 16500 and areaCurrentCnt < 17500:
                        cv2.putText(src, "20 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2) 
                        total += 0.20
                    elif areaCurrentCnt > 11500 and areaCurrentCnt < 12500:
                        cv2.putText(src, "2 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                        total += 0.02
                    elif areaCurrentCnt > 18000 and areaCurrentCnt < 19000:
                        cv2.putText(src, "1 Euro", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                        total += 1.0
                    elif areaCurrentCnt > 8000 and areaCurrentCnt < 9000:
                        cv2.putText(src, "1 Centimo", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                        total += 0.01
                    elif areaCurrentCnt > 14500 and areaCurrentCnt < 15500:
                        cv2.putText(src, "5 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                        total += 0.05
                    elif areaCurrentCnt > 2100 and areaCurrentCnt < 2200:
                        cv2.putText(src, "2 Euros", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                        total += 2.0  
            # # aqui em baixo percorro o array de formas circulares e vou identifica-las por area!
            # if areaCurrentCnt > 22100:
            #     cv2.putText(src, "50 Centimos", (cX - 20, cY - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
                    
        # cv2.putText(img_contours, "center", (cX - 20, cY - 20),
        # 		cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
totalStringForm = str(round(total,2))                        
cv2.putText(src, "Total: " + totalStringForm, (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
cv2.imshow('contornos! Final', src ) 


cv2.waitKey(0)
cv2.destroyAllWindows()

















































