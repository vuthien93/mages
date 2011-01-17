/***
 * Mages: Multiplayer Game Engine for mobile devices
 * Copyright (C) 2008 aksonov
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact: aksonov dot gmail dot com
 *
 * Author: Pavlo Aksonov
 */
package org.aksonov.mages.entities;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Custom implements Parcelable {
	public Serializable obj;
	public static final Parcelable.Creator<Custom> CREATOR = new Parcelable.Creator<Custom>() {
		public Custom createFromParcel(Parcel in) {
			return new Custom(in);
		}

		public Custom[] newArray(int size) {
			return new Custom[size];
		}
	};

	private Custom(Parcel parcel) {
		readFromParcel(parcel);
	}

	private Custom(Serializable obj) {
		this.obj = obj;
	}

	public static Custom create(Serializable obj) {
		if (obj == null){
			throw new IllegalArgumentException("Custom.create accepts only non-null serializable objects");
		}
		return new Custom(obj);
	}

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
		parcel.writeSerializable(obj);
		Custom.dispose(this);
	}

	public void readFromParcel(Parcel parcel) {
		obj = parcel.readSerializable();
	}

	public Object getObject() {
		return obj;
	}

	public void dispose() {
		Custom.dispose(this);
	}
	
	public static void dispose(Custom object){
	}

}
