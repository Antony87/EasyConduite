/*
 * Copyright (C) 2017 Antony Fons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package easyconduite.exception;

/**
 *
 * @author antony
 */
public class RemotePlayableException extends Exception {

    /**
     * Creates a new instance of <code>RemotePlayableException</code> without
     * detail message.
     */
    public RemotePlayableException() {
    }

    /**
     * Constructs an instance of <code>RemotePlayableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RemotePlayableException(String msg) {
        super(msg);
    }

        /**
     * Constructs an instance of <code>RemotePlayableException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     * @param cause
     */
    public RemotePlayableException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
