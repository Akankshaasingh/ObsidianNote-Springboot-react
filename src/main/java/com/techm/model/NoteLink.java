package com.techm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "NoteLink")
public class NoteLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer linkId;

    @ManyToOne
    @JoinColumn(name = "source_note_id")
    private Note sourceNote;

    @ManyToOne
    @JoinColumn(name = "target_note_id")
    private Note targetNote;

    public Integer getLinkId() {
		return linkId;
	}

	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}

	public Note getSourceNote() {
		return sourceNote;
	}

	public void setSourceNote(Note sourceNote) {
		this.sourceNote = sourceNote;
	}

	public Note getTargetNote() {
		return targetNote;
	}

	public void setTargetNote(Note targetNote) {
		this.targetNote = targetNote;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	private String linkType;

    
}
